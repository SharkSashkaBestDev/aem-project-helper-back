package com.csg.asoft.aemprojecthelper.controller;

import com.csg.asoft.aemprojecthelper.model.Component;
//import com.csg.asoft.aemprojecthelper.repository.ComponentRepository;
import com.csg.asoft.aemprojecthelper.utils.MySimpleFileVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ComponentController {
//    @Autowired
//    private ComponentRepository componentRepository;

//    @GetMapping("/components")
//    public Flux<Component> getAllComponents() {
//        return componentRepository.findAll();
//    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/components")
    public Flux<Component> getAllComponentsByPath(@Valid @RequestBody String projectPath) {
        List<Path> allFiles = new ArrayList<>();
        try {
            Path dir = Paths.get(projectPath);

            FileVisitor<Path> fileVisitor = new MySimpleFileVisitor(allFiles);

            Files.walkFileTree(dir, fileVisitor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Component> components = pathsToComponents(allFiles);

        return Flux.fromIterable(components);
    }

    private List<Component> pathsToComponents(final List<Path> paths) {
        List<Component> components = new ArrayList<>();
        paths.forEach(path -> {
            Component component = new Component();
            component.setPath(path.toAbsolutePath().toString());

            try {
                BufferedReader br = new BufferedReader(new FileReader(path.toAbsolutePath().toFile()));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!StringUtils.isEmpty(line)) {
                        String titleStr = "jcr:title=";
                        String groupStr = "componentGroup=";
                        String parentStr = "sling:resourceSuperType=";
                        String descStr = "jcr:description=";
                        String createdStr = "jcr:created=";
                        if (line.contains(titleStr)) {
                            component.setName(line.substring(line.indexOf(titleStr) + titleStr.length() + 1, line.lastIndexOf("\"")));
                        }
                        if (line.contains(groupStr)) {
                            component.setGroup(line.substring(line.indexOf(groupStr) + groupStr.length() + 1, line.lastIndexOf("\"")));
                        }
                        if (line.contains(parentStr)) {
                            component.setParent(line.substring(line.indexOf(parentStr) + parentStr.length() + 1, line.lastIndexOf("\"")));
                        }
                        if (line.contains(descStr)) {
                            component.setParent(line.substring(line.indexOf(descStr) + descStr.length() + 1, line.lastIndexOf("\"")));
                        }
                        if (line.contains(createdStr)) {
                            component.setCreatedAt(Date.valueOf(line.substring(line.indexOf(createdStr) + createdStr.length() + 1, line.lastIndexOf("\""))));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            components.add(component);
        });
        return components;
    }
//    @PostMapping("/components")
//    public Mono<Component> createComponents(@Valid @RequestBody Component component) {
//        return componentRepository.save(component);
//    }

//    @GetMapping("/components/{id}")
//    public Mono<ResponseEntity<Component>> getComponentById(@PathVariable(value = "id") String componentId) {
//        return componentRepository.findById(componentId)
//                .map(savedComponent -> ResponseEntity.ok(savedComponent))
//                .defaultIfEmpty(ResponseEntity.notFound().build());
//    }
//
//    @PutMapping("/components/{id}")
//    public Mono<ResponseEntity<Component>> updateComponent(@PathVariable(value = "id") String componentId,
//                                                   @Valid @RequestBody Component component) {
//        return componentRepository.findById(componentId)
//                .flatMap(existingComponent -> {
//                    existingComponent.setName(component.getName());
//                    existingComponent.setDescription(component.getDescription());
//                    existingComponent.setPath(component.getPath());
//                    existingComponent.setParent(component.getParent());
//                    existingComponent.setGroup(component.getGroup());
//                    return componentRepository.save(existingComponent);
//                })
//                .map(updatedTweet -> new ResponseEntity<>(updatedTweet, HttpStatus.OK))
//                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
//
//    @DeleteMapping("/components/{id}")
//    public Mono<ResponseEntity<Void>> deleteComponent(@PathVariable(value = "id") String componentId) {
//
//        return componentRepository.findById(componentId)
//                .flatMap(existingComponent ->
//                        componentRepository.delete(existingComponent)
//                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
//                )
//                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
//
//    // Components are Sent to the client as Server Sent Events
//    @GetMapping(value = "/stream/components", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<Component> streamAllComponents() {
//        return componentRepository.findAll();
//    }
}
