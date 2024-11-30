package com.codingshuttle.TestingApp.controllers;

import com.codingshuttle.TestingApp.TestContainerConfiguration;
import com.codingshuttle.TestingApp.dto.EmployeeDto;
import com.codingshuttle.TestingApp.entities.Employee;
import com.codingshuttle.TestingApp.repositories.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;


class EmployeeControllerTestIT extends AbstractIntegerationTest {



@Autowired
private EmployeeRepository employeeRepository;

private Employee testEmployee;

private EmployeeDto testEmployeeDto;

@BeforeEach
void setUp(){
    testEmployee=Employee.builder().id(1L).email("anuj@gmail.com").name("anuj").salary(200L).build();

    testEmployeeDto=EmployeeDto.builder().id(1L).email("anuj@gmail.com").name("anuj").salary(200L).build();

    employeeRepository.deleteAll();
}

    @Test
    void testGetEmployeeById_Success(){
     Employee savedEmployee=employeeRepository.save(testEmployee);
     webTestClient.get().uri("/employees/{id}",savedEmployee.getId()).exchange().expectStatus().isOk()
             .expectBody(EmployeeDto.class)
             .value(employeeDto -> {
                 Assertions.assertThat(employeeDto.getEmail()).isEqualTo(savedEmployee.getEmail());
                 Assertions.assertThat(employeeDto.getId()).isEqualTo(savedEmployee.getId());
             });

    }

    @Test
    void testGetEmployeeById_Failure(){
      webTestClient.get().uri("/employees/1").exchange().expectStatus().isNotFound();
    }



    @Test
    void testCreateNewEmployee_whenEmployeeAlreadyExists_thenThrowException(){
    Employee savedEmployee=employeeRepository.save(testEmployee);

    webTestClient.post().uri("/employees").bodyValue(testEmployeeDto).exchange().expectStatus().is5xxServerError();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeDoesNotExists_thenCreateEmloyee(){
     webTestClient.post().uri("/employees").bodyValue(testEmployeeDto).exchange().expectStatus().isCreated().expectBody().jsonPath("$.email").isEqualTo(testEmployeeDto.getEmail()).jsonPath("$.name").isEqualTo(testEmployeeDto.getName());
    }
    
    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException(){
     webTestClient.put().uri("/employees/999").bodyValue(testEmployeeDto).exchange().expectStatus().isNotFound();
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateTheEmail_thenThrowException(){
    Employee savedEmployee=employeeRepository.save(testEmployee);
    testEmployeeDto.setName("randomname");
    testEmployeeDto.setEmail("random@gmail.com");

    webTestClient.put().uri("/employees/{id}",savedEmployee.getId()).bodyValue(testEmployeeDto).exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateEmployee_whenEmployeeIsvalid_thenUpdateEmployee(){
        Employee savedEmployee=employeeRepository.save(testEmployee);
        testEmployeeDto.setName("randomname");
        testEmployeeDto.setSalary(250L);

        webTestClient.put().uri("/employees/{id}",savedEmployee.getId()).bodyValue(testEmployeeDto).exchange()
                .expectStatus().isOk().expectBody().jsonPath("$.name").isEqualTo(testEmployeeDto.getName()).jsonPath("$.salary").isEqualTo(testEmployeeDto.getSalary());
    }


    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException(){

    webTestClient.delete().uri("/employees/1").exchange().expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployee_whenEmployeeExists_thenDeleteEmployee(){
    Employee savedEmployee=employeeRepository.save(testEmployee);

    webTestClient.delete().uri("/employees/{id}",savedEmployee.getId()).exchange().expectStatus().isNoContent().expectBody(Void.class);
  }
}