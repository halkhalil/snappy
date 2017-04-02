package io.joshworks.snappy.extras.ssr.server.service;


import io.joshworks.snappy.extras.ssr.Instance;
import io.joshworks.snappy.rest.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.joshworks.snappy.extras.ssr.SSRKeys.SSR_LOGGER;

/**
 * Created by Josue on 15/06/2016.
 */
public class ServiceControl {

    private static final Map<String, Service> store = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(SSR_LOGGER);
//    private static final Map<String, ServiceConfig> store = new ConcurrentHashMap<>();

    //returns a copy of all the services, including the disabled ones
    public Service getService(String name) {
        Service service = store.get(name);
        if (service == null) {
            throw RestException.notFound("Service not found for name '" + name + "'");
        }
        return service;
    }

    public Set<Service> getServices() {
        return new HashSet<>(store.values());
    }

    public Instance register(Instance instance) {
        if (instance == null) {
            throw RestException.badRequest("Invalid instance");
        }
        if (instance.getAddress() == null || instance.getAddress().trim().isEmpty()) {
            throw RestException.badRequest("'address' must be provided");
        }
//        if (instance.getId() == null || instance.getId().trim().isEmpty()) {
//            throw RestException.badRequest("'id' must be provided");
//        }
        if (instance.getSince() == 0) {
            instance.setSince(System.currentTimeMillis());
        }
        instance.setId(UUID.randomUUID().toString().substring(0,8));
        instance.setState(Instance.State.DOWN);

        String service = instance.getName();

        if (!store.containsKey(service)) {
            store.put(service, new Service(service));
        }
        Service serviceConfig = store.get(service);

        serviceConfig.addInstance(instance);
        logger.info("New service registered {} ", instance);

        return instance;
    }

    public Instance updateInstanceState(String instanceId, Instance.State newState) {
        Optional<Instance> first = instances().stream()
                .filter(i -> i.getId().equals(instanceId))
                .findFirst();

        if (!first.isPresent()) {
            throw RestException.badRequest("Instance not found for id '" + instanceId + "'");
        }

        Instance instance = first.get();
        instance.updateInstanceState(newState);
        return instance;
    }

    public List<Instance> instances() {
        return store.values().stream()
                .flatMap(l -> l.getInstances().stream()).collect(Collectors.toList());
    }

    public void addLink(String client, String target) {
        Service targetService = store.get(target);
        Service sourceService = store.get(client);

        if (targetService == null) {
            throw RestException.badRequest("Service " + target + " not found");
        }
        if (sourceService == null) {
            throw RestException.badRequest("Service " + client + " not found");
        }

        sourceService.getLinks().add(target);
    }

}