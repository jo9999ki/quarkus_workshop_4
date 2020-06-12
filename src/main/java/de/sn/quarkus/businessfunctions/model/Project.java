package de.sn.quarkus.businessfunctions.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;

@Entity
@Schema(name="Project", description="project = container of business function items") //OpenAPI
public class Project extends PanacheEntity{
	
	@Schema(description = "name of the item", maxLength = 20, required = true, example = "My magic project")	
	@NotNull(message="item name cannot be blank") //Validation
	@Column(name= "name", length = 20, nullable = false)//Database
	public String name;
	
	//Items in project
	@Schema(description = "list of all items in that project", required = false)
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	public List<Item> items;
	
	//Customized queries
	public static List<Project> findAllByNameLike(String name){
        return find("name LIKE concat('%', :name, '%')", 
                Parameters.with("name", name)).list();
    }
}
