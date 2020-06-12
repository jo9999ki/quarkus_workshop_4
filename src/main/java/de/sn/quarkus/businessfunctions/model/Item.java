package de.sn.quarkus.businessfunctions.model;
import java.util.List;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;

@Entity
@Schema(name="Item", description="business function item") //OpenAPI
public class Item extends PanacheEntity{
	
	@NotNull(message="item name cannot be blank") //Validation
	@Length(min = 1, max = 20, message="item name must be between 1 and 20")//Validation
	@Column(name= "name", length = 20, nullable = false)//Database
	public String name;
	
	@Length(min = 1, max = 255, message="image URL must be between 1 and 255")//Validation
	@Column(name= "imageurl", length = 255, nullable = true)//Database
	public String imageURL;
	
	@NotNull(message="item level cannot be blank") //Validation
	@Min(0)//Validation
	@Max(5)//Validation
	@Column(name= "level", nullable = false)//Database
	public Integer level;

	//Projects containing items
	@ManyToOne
	@JsonbTransient
	public Project project; 
	//Lower items in hierarchy
	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	public List<Item> items;
	
	//Higher item in hierarchy
	@ManyToOne
    @JsonbTransient
    public Item item; 
	
	//Customized queries...
	public static PanacheQuery<PanacheEntityBase> findByLevelAndProjectId(Integer level, Long projectid){
		 return find("level = :level and project.id = :projectid",
	         Parameters.with("level", level).and("projectid", projectid));
	}
	
	public static PanacheQuery<PanacheEntityBase> findByProjectId(Long projectid){
		 return find("project.id", projectid);
	}
}
