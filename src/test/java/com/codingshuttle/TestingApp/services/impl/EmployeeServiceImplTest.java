package com.codingshuttle.TestingApp.services.impl;

import com.codingshuttle.TestingApp.TestContainerConfiguration;
import com.codingshuttle.TestingApp.dto.EmployeeDto;
import com.codingshuttle.TestingApp.entities.Employee;
import com.codingshuttle.TestingApp.exceptions.ResourceNotFoundException;
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
//for testing the service layer we have to go with below annotation as @DataJpaTest only works for repository layer
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

//Testing for the happy case when employee is present
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



    //Testing for the exception case when employee is  not present
@Test
void testGetEmployeeById_WhenEmployeeIsNotPresent_thenThrowException(){
//    assign/given
    when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

//    act and assert
    Assertions.assertThatThrownBy(()->employeeService.getEmployeeById(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Employee not found with id: 1");
    verify(employeeRepository).findById(1L);


//    assert
}





//Testing for the happy case when employee is valid and creation of employee is done

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

//testing for the exception case when employee cannot be created as it is already existing in our system
@Test
void testCreateNewEmployee_WhenAttemptingToCreateEmployeeWithExistingEmail_ThenThrowException(){
//    arrange
    when(employeeRepository.findByEmail(mockemployeeDto.getEmail())).thenReturn(List.of(mockemployee));


//    act and assert
    Assertions.assertThatThrownBy(()->employeeService.createNewEmployee(mockemployeeDto))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Employee already exists with email: "+mockemployee.getEmail());

    verify(employeeRepository).findByEmail(mockemployee.getEmail());

    verify(employeeRepository,never()).save(any());


}
//Testing for exception case when employee does not exist
@Test
void testUpdateEmployee_whenEmployeeDoesNotExist_thenThrowException(){

//    assign
    when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(()->employeeService.updateEmployee(1L,mockemployeeDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Employee not found with id: 1");

    verify(employeeRepository).findById(1L);
    verify(employeeRepository,never()).save(any());


}

//Testing for exception case when attempting to update email but our code won't let us to update email
@Test
void testUpdateEmployee_whenAttemptingToUpdateEmail_thenThrowException(){
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockemployee));

    mockemployeeDto.setName("Random");
    mockemployeeDto.setEmail("random@gmail.com");

    Assertions.assertThatThrownBy(()->employeeService.updateEmployee(mockemployeeDto.getId(),mockemployeeDto))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("The email of the employee cannot be updated");
    verify(employeeRepository).findById(1L);
    verify(employeeRepository,never()).save(any());

}


//Testing for the happy case where we will be able to update the existing employee
@Test
void testUpdateEmplyee_whenValidEmployee_thenUpdateEmployee(){
//    arrange
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockemployee));
    mockemployeeDto.setName("random");
    mockemployeeDto.setSalary(188L);

    Employee newEmployee=modelMapper.map(mockemployeeDto,Employee.class);
//    save() is going to save and return the employee with updated details
//    thats why for mocking purpose we are creating newemployee with the updated details
//    and mapping it to Employee

    when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);

//    act
    EmployeeDto updatedEmployeeDto=employeeService.updateEmployee(mockemployeeDto.getId(),mockemployeeDto);

    Assertions.assertThat(updatedEmployeeDto).isEqualTo(mockemployeeDto);
//    updatedEmployeeDto is that we are getting from employeeService.updateEmployee()
//    and mockEmployeeDto is that we are passing to it
//    if both are equal that means updation is done successfully
}

//testing for exception case
@Test
void testDeleteEmployee_whenEmployeeDoesNotExist_ThenThrowException(){
    when(employeeRepository.existsById(1L)).thenReturn(false);

    Assertions.assertThatThrownBy(()->employeeService.deleteEmployee(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Employee not found with id: 1");

    verify(employeeRepository,never()).deleteById(anyLong());
}

//Testing for happy case when we are able to delete the employee
@Test
void testDeleteEmployee_whenEmployeeIsValid_thenDeleteEmployee(){
    when(employeeRepository.existsById(1L)).thenReturn(true);

    Assertions.assertThatCode(()->employeeService.deleteEmployee(1L)).doesNotThrowAnyException();
    verify(employeeRepository).deleteById(1L);
}




}