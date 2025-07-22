package com.bank.customer.features.person;

import com.bank.customer.common.constants.ApiConstants;
import com.bank.customer.features.person.dto.CreatePersonDto;
import com.bank.customer.features.person.dto.PersonDto;
import com.bank.customer.features.person.dto.UpdatePersonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiConstants.API_V1_VERSION + "persons")
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<PersonDto> create(@RequestBody CreatePersonDto crreatePersonDto) {
        PersonDto createdDto = personService.create(crreatePersonDto);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PersonDto>> getAll() {
        List<PersonDto> dtos = personService.findAll();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDto> getById(@PathVariable("id") UUID id) {
        return personService.findByTransactionUuid(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDto> update(@PathVariable("id") UUID id, @RequestBody UpdatePersonDto updatePersonDto) {
        PersonDto updatedDto = personService.update(id, updatePersonDto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }
}