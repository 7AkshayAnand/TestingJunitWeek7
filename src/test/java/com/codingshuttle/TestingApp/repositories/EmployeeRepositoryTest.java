package com.codingshuttle.TestingApp.repositories;

import com.codingshuttle.TestingApp.TestContainerConfiguration;
import com.codingshuttle.TestingApp.entities.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.List;
import org.assertj.core.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestContainerConfiguration.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class EmployeeRepositoryTest {
    @Autowired
    private  EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    void setUp(){
        employee=Employee.builder().id(1L).name("Anuj").email("anuj@gmail.com").salary(1000L).build();
    }



    @Test
    void testFindByEmail_whenEmailIsvalid_thenReturnEmployee() {
//   assign/given
    employeeRepository.save(employee);
//    act
    List<Employee> employeeList=employeeRepository.findByEmail(employee.getEmail());
//assert
   Assertions.assertThat(employeeList).isNotNull();
   Assertions.assertThat(employeeList).isNotEmpty();
   Assertions.assertThat(employeeList.get(0).getEmail()).isEqualTo(employee.getEmail());
    }

    @Test
    void testFindByEmail_whenEmailIsNotFound_thenReturnEmptyEmployeeList(){

//         given/assign
        String email="notpresent@gmail.com";
//        act
        List<Employee> employeeList=employeeRepository.findByEmail(email);
//assert
        Assertions.assertThat(employeeList).isNotNull();
        Assertions.assertThat(employeeList).isEmpty();

    }
}