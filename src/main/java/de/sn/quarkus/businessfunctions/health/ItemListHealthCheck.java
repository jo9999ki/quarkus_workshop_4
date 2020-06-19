package de.sn.quarkus.businessfunctions.health;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import de.sn.quarkus.businessfunctions.resources.ProjectResource;

@Readiness
@ApplicationScoped
public class ItemListHealthCheck implements HealthCheck {

    @Inject
    ProjectResource projectResource;

    @Override
    public HealthCheckResponse call() {
    	projectResource.getPagableList(0,2 );
        return HealthCheckResponse.named("REST method + db health check (list)").up().build();
    }
}

