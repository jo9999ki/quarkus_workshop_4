package de.sn.quarkus.businessfunctions.resources;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import de.sn.quarkus.businessfunctions.exception.BusinessException;
import de.sn.quarkus.businessfunctions.exception.ErrorsResponse;
import de.sn.quarkus.businessfunctions.exception.RecordNotFoundException;
import de.sn.quarkus.businessfunctions.model.Item;
import de.sn.quarkus.businessfunctions.model.Project;
import io.quarkus.panache.common.Page;

@Tag(name= "Items") //OpenAPI
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class ItemResource {
	
	@Inject
    EntityManager em;
	
	@Inject 
	Validator validator;

	@GET
	@Path("/project/{projectid}")
	@Operation(summary = "List of items of a certain project")
	@Parameters({
		@Parameter(name = "projectid", in = ParameterIn.PATH,required = true, 
    			description = "project id, value >= 0"),
		@Parameter(name = "pageNum", in = ParameterIn.QUERY,required = false, 
    			description = "number of requested page, value >= 0"),
    	@Parameter(name = "pageSize", in = ParameterIn.QUERY,required = false, 
		description = "size of page (number of records), value >= 0" )
    	})
    @APIResponse(responseCode = "200", description = "Total list of items for that project", 
    		content = @Content(mediaType = "application/json",
            		schema = @Schema(type = SchemaType.ARRAY, implementation = Item.class)))
    public Response getPagableItemListForProject(
    		@PathParam("projectid") @NotNull Long projectid,
    		@QueryParam("pageNum") @DefaultValue("0") @Min(0) int pageNum, 
    		@QueryParam("pageSize") @DefaultValue("10") @Min(0) int pageSize) {
    	
		long timestamp = System.currentTimeMillis();
		List<Item> items = Item.findByProjectId(projectid).page(Page.of(pageNum, pageSize)).list();
    	return Response
    			.ok(items)
        		.header("responsetime", (System.currentTimeMillis() - timestamp))
    			.build();
    }
	
	@GET
	@Path("/project/{projectid}/level/{level}")
	@Operation(summary = "List of items of a certain project on given level, sub items will be shown in hierarchy too")
	@Parameters({
		@Parameter(name = "projectid", in = ParameterIn.PATH,required = true, 
    			description = "project id, value >= 0"),
		@Parameter(name = "level", in = ParameterIn.PATH,required = true, 
		description = "level of items, value >= 0"),
		@Parameter(name = "pageNum", in = ParameterIn.QUERY,required = false, 
    			description = "number of requested page, value >= 0"),
    	@Parameter(name = "pageSize", in = ParameterIn.QUERY,required = false, 
		description = "size of page (number of records), value >= 0" )
    	})
	 @APIResponse(responseCode = "200", description = "List of items for that project and item level", 
		content = @Content(mediaType = "application/json",
     		schema = @Schema(type = SchemaType.ARRAY, implementation = Item.class)))
    public Response getPagableItemListForProjectAndLevel(
    		@PathParam("projectid") @NotNull Long projectid,
    		@PathParam("level") @NotNull Integer level,
    		@QueryParam("pageNum") @DefaultValue("0") @Min(0) int pageNum, 
    		@QueryParam("pageSize") @DefaultValue("10") @Min(0) int pageSize) {
    	
		long timestamp = System.currentTimeMillis();
		List<Item> items = Item.findByLevelAndProjectId(level, projectid).page(Page.of(pageNum, pageSize)).list();
    	return Response
    			.ok(items)
        		.header("responsetime", (System.currentTimeMillis() - timestamp))
    			.build();
    }
	
	@GET
    @Path("/{id}")
    @Operation(summary = "Get item for id")
    @Parameters({
    	@Parameter(name = "id", in = ParameterIn.PATH, required = true, 
    			description = "unique item identifier")})
    @APIResponse(responseCode = "200", description = "item for id", 
    		content = @Content(mediaType = "application/json",
            		schema = @Schema(type = SchemaType.ARRAY, implementation = Item.class)))
	public Response getItemById(
    		@PathParam("id") @NotNull Long id) {
    	long timestamp = System.currentTimeMillis();
    	Item myItem = Item.findById(id);
    	if (myItem != null) {
    	   	return Response
        			.ok(myItem)
            		.header("responsetime", (System.currentTimeMillis() - timestamp))
        			.build();
    	}
    	return Response.status(Response.Status.NOT_FOUND).build();
    }
	
	@POST
	@Path("/project/{projectid}")
    @Operation(summary = "Create new main item for given project")
    @APIResponse(responseCode = "201", description = "Created item",
                 content = @Content(mediaType = "application/json",
                 	schema = @Schema(implementation = Item.class)))
    @APIResponse(responseCode = "400", description = "Invalid request data",
    content = @Content(mediaType = "application/json",
 	schema = @Schema(implementation = ErrorsResponse.class)))
    @APIResponse(responseCode = "500", description = "Unknown error", 
	content = @Content(mediaType = "application/json",
    		schema = @Schema(implementation = String.class)))    
	public Response addMainItem(
			@PathParam("projectid") @NotNull Long projectid,
			@Valid Item item) throws Exception{
		
			Project project = Project.findById(projectid);
			if (project == null) {	
				throw new RecordNotFoundException("Project with id " + projectid +" does not exist");
			}
			item.id = null;
			item.project = project;
			Item itemAbove = null;
			if (item.item != null) {
				itemAbove = Item.findById(item.item.id);
				if (itemAbove == null) {
					throw new RecordNotFoundException("Item with id " + item.item.id +" does not exist");
				}
				item.item = itemAbove;
			}
			Item storedItem = em.merge(item);
			project.items.add(storedItem);
			project.persist();
			if (itemAbove !=null) {
				itemAbove.items.add(storedItem);
				itemAbove.persist();
			}
			return Response.status(Response.Status.CREATED).entity(storedItem).build();		
	}
	@POST
	@Path("/project/{projectid}/item/{itemabove}")
    @Operation(summary = "Create sub item for given project and existing item")
    @APIResponse(responseCode = "201", description = "Created sub item",
                 content = @Content(mediaType = "application/json",
                 	schema = @Schema(implementation = Item.class)))
    @APIResponse(responseCode = "400", description = "Invalid request data",
    content = @Content(mediaType = "application/json",
 	schema = @Schema(implementation = ErrorsResponse.class)))
    @APIResponse(responseCode = "500", description = "Unknown error", 
	content = @Content(mediaType = "application/json",
    		schema = @Schema(implementation = String.class)))    
	public Response addSubItem(
			@PathParam("projectid") @NotNull Long projectid,
			@PathParam("itemabove") @NotNull Long itemAboveId,
			@Valid Item item) throws Exception{
		
			Project project = Project.findById(projectid);
			if (project == null) {	
				throw new RecordNotFoundException("Project with id " + projectid +" does not exist");
			}
			
			Item itemAbove = Item.findById(itemAboveId);
			if (itemAbove == null) {
				throw new RecordNotFoundException("Item with id " + item.item.id +" does not exist");
			}
			item.id = null;
			item.item = itemAbove; //Addmain item			
			item.project = project; //Add item to project
			Item storedItem = em.merge(item);

			return Response.status(Response.Status.CREATED)
					.entity(storedItem)
					.build();		
	}
	
	@PUT
    @Operation(summary = "Update item attributes (update of item attributes only, no update of project or other items in hierarchy too")
    @APIResponse(responseCode = "201", description = "updated item",
                 content = @Content(mediaType = "application/json",
                 	schema = @Schema(implementation = Item.class)))
    @APIResponse(responseCode = "400", description = "Invalid request data",
    content = @Content(mediaType = "application/json",
 	schema = @Schema(implementation = ErrorsResponse.class)))
    @APIResponse(responseCode = "500", description = "Unknown error", 
	content = @Content(mediaType = "application/json",
    		schema = @Schema(implementation = String.class)))    
	public Response change(Item item) {
		Item myItem  = Item.findById(item.id);
    	if (myItem != null) {
    		myItem.name = item.name;
    		myItem.imageURL = item.imageURL;
    		myItem.level = item.level;
    		Item storedItem = em.merge(myItem);
    		return Response.status(Response.Status.OK).entity(storedItem).build();
    	}else {
    		return Response.status(Response.Status.NOT_FOUND).build();
    	}
	}
	
	@DELETE
    @Path("/{id}")
    @Operation(summary = "delete item (deletion of items only, if no sub items contained. Sub items must be deleted before")
    @Parameters({
    	@Parameter(name = "id", in = ParameterIn.PATH, required = true, 
    			description = "unique item identifier")})
    @APIResponse(responseCode = "204", description = "item for given id has been deleted")
    @APIResponse(responseCode = "400", description = "Invalid request data",
    content = @Content(mediaType = "application/json",
 	schema = @Schema(implementation = ErrorsResponse.class)))
    @APIResponse(responseCode = "500", description = "Unknown error", 
	content = @Content(mediaType = "application/json",
    		schema = @Schema(implementation = String.class)))    
	public Response delete(@PathParam("id") Long id) throws Exception{
    	Item item  = Item.findById(id);
    	if (item == null) {
    		return Response.status(Response.Status.NOT_FOUND).build();
    	}
    	
    	if (item.items.size() > 0) {
    		throw new BusinessException("40009", "Item contains sub items, which have to be deleted before!");
    	}    		

    	//Delete item from project
		item.project.items.remove(item);

		//Delete item from main items item list
		if (item.item != null) {
			if (item.item.items.size() > 0) {
				item.item.items.remove(item);
			}
		}
		
		item.delete();
		
    	return Response
        		.status(Response.Status.NO_CONTENT)
        		.build();    	
    }
	
}