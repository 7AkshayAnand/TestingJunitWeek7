package com.codingshuttle.TestingApp.services.impl;

import com.codingshuttle.TestingApp.TestContainerConfiguration;
import com.codingshuttle.TestingApp.dto.EmployeeDto;
import com.codingshuttle.TestingApp.entities.Employee;
import com.codingshuttle.TestingApp.repositories.EmployeeRepository;
import com.codingshuttle.TestingApp.services.EmployeeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Import(TestContainerConfiguration.class)
@ExtendWith(MockitoExtension.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class EmployeeServiceImplTest {
@Mock
private EmployeeRepository employeeRepository;


@Spy
private ModelMapper modelMapper;
//above both are the dependency needed for the test

@InjectMocks
private EmployeeServiceImpl employeeService;
//now this is the actual class that goes under the test

    private Employee mockemployee;
    private EmployeeDto mockemployeeDto;
@BeforeEach
void setUp(){
    mockemployee =Employee.builder().id(1L).email("anuj@gmail.com").name("anuj").salary(200L).build();
    mockemployeeDto=modelMapper.map(mockemployee,EmployeeDto.class);
}


@Test
void testGetEmployeeById_WhenEmployeeIdIsPresent_ThenReturnEmployeeDto(){
  Long id=mockemployee.getId();


//  assign

    when(employeeRepository.findById(id)).thenReturn(Optional.of(mockemployee));
//  stubbing
//  act

    EmployeeDto employeeDto=employeeService.getEmployeeById(id);
//    assert
    Assertions.assertThat(employeeDto.getId()).isEqualTo(id);
    Assertions.assertThat(employeeDto.getEmail()).isEqualTo(mockemployee.getEmail());
//    verify(employeeRepository).findById(id);
//     verify(employeeRepository).save(null);
//    verify(employeeRepository,times(2)).findById(id);
    verify(employeeRepository,atLeast(1)).findById(id);
    verify(employeeRepository,only()).findById(id);
}



@Test
void testCreateNewEmployee_WhenValidEmployee_ThenCreateNewEmployee(){
//    assign
//    whenever we are going to call findByemail() it will return the empty list so that we can save this employee as per the actual implementation of the createnewemployee in EmployeeServiceImpl class inside the service pakcage
    when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());

    when(employeeRepository.save(any(Employee.class))).thenReturn(mockemployee);

//    act
//because our createnewemployee method returns the DTO only so we have to recieve in dto
    EmployeeDto employeeDto=employeeService.createNewEmployee(mockemployeeDto);




//    assert




    Assertions.assertThat(employeeDto).isNotNull();
    Assertions.assertThat(employeeDto.getEmail()).isEqualTo(mockemployeeDto.getEmail());
    verify(employeeRepository).save(any(Employee.class));

    ArgumentCaptor<Employee> employeeArgumentCaptor=ArgumentCaptor.forClass(Employee.class);
    verify(employeeRepository).save(employeeArgumentCaptor.capture());
//    it captures the object that is passed in save method as argument
    Employee capturedEmployee=employeeArgumentCaptor.getValue();
//    here we are cheking that the value that is passed in save() is the same value that we have passed from here or not
    Assertions.assertThat(capturedEmployee.getEmail()).isEqualTo(mockemployee.getEmail());
}


}