package com.batch.step;

import com.batch.entities.Person;
import com.batch.service.IPersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class PersonItemWriterStep implements ItemWriter<Person> {

    @Autowired
    private IPersonService personService;

    @Override
    public void write(Chunk<? extends Person> chunk) throws Exception {

        log.info("Ingreso al writer");
        chunk.forEach(person -> log.info(person.toString()));

        List<Person> persons = chunk.getItems().stream().map(person -> Person.builder()
                .name(person.getName())
                .lastName(person.getLastName())
                .age(person.getAge())
                .build()).toList();



        personService.saveAll(persons);

    }
}
