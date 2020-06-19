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

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
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
import de.sn.quarkus.businessfunctions.model.Item;
import de.sn.quarkus.businessfunctions.model.Project;
import io.quarkus.panache.common.Page;

@Tag(name= "Projects") //OpenAPI
@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class ProjectResource {
	
	@Inject EntityManager em;
	@Inject Validator validator;

	@GET
	//OpenAPI
    @Operation(summary = "List of projects and their contained items")
    @Parameters({
    	@Parameter(name = "pageNum", in = ParameterIn.QUERY,required = false, 
    			description = "number of requested page, value >= 0"),
    	@Parameter(name = "pageSize", in = ParameterIn.QUERY,required = false, 
		description = "size of page (number of records), value >= 0" )
    	})
    @APIResponse(responseCode = "200", description = "List of projects", 
    		content = @Content(mediaType = "application/json",
            		schema = @Schema(type = SchemaType.ARRAY, implementation = Project.class)))
	@Timed(name = "ListTimer", 
		description = "measures response time, request count and throughput", 
		unit = MetricUnits.MILLISECONDS)
	public Response getPagableList( 
    		@QueryParam("pageNum") @DefaultValue("0") @Min(0) int pageNum, 
    		@QueryParam("pageSize") @DefaultValue("10") @Min(0) int pageSize) {
    	
		long timestamp = System.currentTimeMillis();
		List<Project> projects = Project
    			.findAll().page(Page.of(pageNum, pageSize)).list();
    	return Response
    			.ok(projects)
        		.header("responsetime", (System.currentTimeMillis() - timestamp))
    			.build();
    }
	
	@GET
    @Path("/{id}")
    @Operation(summary = "Get project for id including contained items")
    @Parameters({
    	@Parameter(name = "id", in = ParameterIn.PATH, required = true, 
    			description = "unique project identifier")})
    @APIResponse(responseCode = "200", description = "Project for id", 
    		content = @Content(mediaType = "application/json",
            		schema = @Schema(type = SchemaType.ARRAY, implementation = Project.class)))
    @APIResponse(responseCode = "400", description = "Invalid request data",
    content = @Content(mediaType = "application/json",
 	schema = @Schema(implementation = ErrorsResponse.class)))
    @APIResponse(responseCode = "500", description = "Unknown error", 
	content = @Content(mediaType = "application/json",
    		schema = @Schema(implementation = String.class)))    
	public Response getProjectById(
    		@PathParam("id") @NotNull Long id) {
    	long timestamp = System.currentTimeMillis();
    	Project myProject = Project.findById(id);
    	if (myProject != null) {
    	   	return Response
        			.ok(myProject)
            		.header("responsetime", (System.currentTimeMillis() - timestamp))
        			.build();
    	}
    	return Response.status(Response.Status.NOT_FOUND).build();
    }
	
	@POST
	@Operation(summary = "Create new project (creation if project without contained items only)")
    @APIResponse(responseCode = "201", description = "Created project",
                 content = @Content(mediaType = "application/json",
                 	schema = @Schema(implementation = Project.class)))
    @APIResponse(responseCode = "400", description = "Invalid request data",
    content = @Content(mediaType = "application/json",
 	schema = @Schema(implementation = ErrorsResponse.class)))
    @APIResponse(responseCode = "500", description = "Unknown error", 
	content = @Content(mediaType = "application/json",
    		schema = @Schema(implementation = String.class)))    
	public Response add(@Valid Project project) {
		project.id = null;
		//Items will not be stored in this method
		if (project.items != null) project.items.clear();
		Project storedProject = em.merge(project);
		return Response.status(Response.Status.CREATED).entity(storedProject).build();
	}
	
	@PUT
	@Operation(summary = "Update project (update on project level only, no update of items)")
    @APIResponse(responseCode = "201", description = "updated project",
                 content = @Content(mediaType = "application/json",
                 	schema = @Schema(implementation = Project.class)))
    @APIResponse(responseCode = "400", description = "Invalid request data",
    content = @Content(mediaType = "application/json",
 	schema = @Schema(implementation = ErrorsResponse.class)))
    @APIResponse(responseCode = "500", description = "Unknown error", 
	content = @Content(mediaType = "application/json",
    		schema = @Schema(implementation = String.class)))    
	public Response change(@Valid Project project) {
	 	Project myProject  = Project.findById(project.id);
    	if (myProject != null) {
    		myProject.name = project.name;
    		myProject.persist();
    		return Response.status(Response.Status.OK).entity(myProject).build();
    	}else {
    		return Response.status(Response.Status.NOT_FOUND).build();
    	}
	}
	
	@DELETE
    @Path("/{id}")
    @Operation(summary = "delete project (deletion of project only, items have to be deleted before")
    @Parameters({
    	@Parameter(name = "id", in = ParameterIn.PATH, required = true, 
    			description = "unique project identifier")})
    @APIResponse(responseCode = "204", description = "project for given id has been deleted")
    @APIResponse(responseCode = "400", description = "Invalid request data",
    content = @Content(mediaType = "application/json",
 	schema = @Schema(implementation = ErrorsResponse.class)))
	@APIResponse(responseCode = "404", description = "project for given id has not been found",
    content = @Content(mediaType = "application/json",
 	schema = @Schema(implementation = ErrorsResponse.class)))
    @APIResponse(responseCode = "500", description = "Unknown error", 
	content = @Content(mediaType = "application/json",
    		schema = @Schema(implementation = String.class)))    
	public Response delete(@PathParam("id") Long id) throws Exception{
    	Project project  = Project.findById(id);
    	if (project != null) {
    		List<Item> items = Item.findByProjectId(id).list();
    		if (items != null){
    			if (items.size() > 0) throw new BusinessException("40009","Project contains items, which must be deleted before!");
			}
    		project.delete();
        	return Response
            		.status(Response.Status.NO_CONTENT)
            		.build();
    	} else {
    		return Response.status(Response.Status.NOT_FOUND).build();
		}    	
    }
}