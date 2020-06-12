package de.sn.quarkus.businessfunctions.openapi;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
	    tags = {
	            @Tag(name="Projects", description="Projects containing business function item hierarchy"),
	            @Tag(name="Items", description="Single business function items")
	    },
	    info = @Info(
	        title="Business Functions API",
	        description = "OpenAPI document structure standard: version 3.0",
	        version = "1.0.0",
	        contact = @Contact(
	            name = "Business Function API Github side",
	            url = "https://github.com/jo9999ki/quarkus_workshop_3.git",
	            email = "jochen_kirchner@yahoo.com"),
	        license = @License(
	            name = "Apache 2.0",
	            url = "http://www.apache.org/licenses/LICENSE-2.0.html")),
		servers = {
		        @Server(url = "http://localhost:8080")
		}
		
	)
public class OpenAPIApplicationLevelConfiguration extends Application{

}
