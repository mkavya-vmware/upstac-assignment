package org.upgrad.upstac.testrequests.consultation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;


@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    Logger log = LoggerFactory.getLogger(ConsultationController.class);


    @Autowired
    private TestRequestUpdateService testRequestUpdateService;

    @Autowired
    private TestRequestQueryService testRequestQueryService;


    @Autowired
    TestRequestFlowService testRequestFlowService;

    @Autowired
    private UserLoggedInService userLoggedInService;


    @GetMapping("/in-queue")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForConsultations() {

        // Implement this method


        //Implement this method to get the list of test requests having status as 'LAB_TEST_COMPLETED'
        // make use of the findBy() method from testRequestQueryService class
        //return the result
        // For reference check the method getForTests() method from LabRequestController class

        return testRequestQueryService.findBy(RequestStatus.LAB_TEST_COMPLETED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForDoctor() {

        //Implement this method

        // For reference check the method getForTests() method from LabRequestController class

        // Create an object of User class and store the current logged in user first
        User doctor = getLoggedInUser();

        //Make use of the findByDoctor() method from testRequestQueryService class to get the list
        //Implement this method to return the list of test requests assigned to current doctor(make use of the above created User object)
        return testRequestQueryService.findByDoctor(doctor);
    }


    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/assign/{id}")
    public TestRequest assignForConsultation(@PathVariable Long id) {

        // Implement this method

        // Implement this method to assign a particular test request to the current doctor(logged in user)
        // For reference check the method assignForLabTest() method from LabRequestController class
        try {
            //Create an object of User class and get the current logged in user
            User current_doctor = getLoggedInUser();

            //Create an object of TestRequest class and use the assignForConsultation() method of testRequestUpdateService to assign the particular id to the current user
            TestRequest testRequest = testRequestUpdateService.assignForConsultation(id, current_doctor);

            // return the above created object
            return testRequest;

        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }

    private User getLoggedInUser() {
        return userLoggedInService.getLoggedInUser();
    }


    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/update/{id}")
    public TestRequest updateConsultation(@PathVariable Long id, @RequestBody CreateConsultationRequest testResult) {

        // Implement this method

        // Implement this method to update the result of the current test request id with test doctor comment
        //to update the current test request id with the testResult details by the current user(object created)
        // For reference check the method updateLabTest() method from LabRequestController class

        try {
            // Create an object of the User class to get the logged in user
            User current_doctor = getLoggedInUser();

            // Create an object of TestResult class and make use of updateConsultation() method from testRequestUpdateService class
            TestRequest testRequest = testRequestUpdateService.updateConsultation(id, testResult, current_doctor);
            return testRequest;

        } catch (ConstraintViolationException e) {
            throw asConstraintViolation(e);
        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }


}
