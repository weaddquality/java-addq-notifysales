package se.addq.notifysales.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.addq.notifysales.cinode.CinodeApi;
import se.addq.notifysales.cinode.model.AssignmentResponse;
import se.addq.notifysales.cinode.model.ProjectList;
import se.addq.notifysales.cinode.model.ProjectResponse;
import se.addq.notifysales.utils.SleepUtil;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
class AssignmentHandler {

    private boolean fetchProjects = true;

    @Value("${cinode.projects.group.size}")
    private int numberOfProjectsToFetch;

    @Value("${slack.notification.before.weeks}")
    private int weeksBeforeAssignmentEndsToNotify;
    @Value("${slack.notification.after.weeks}")
    private int weeksAfterAssignmentEndsToNotify;


    private final List<Integer> projectsToFetch = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CinodeApi cinodeApi;


    @Autowired
    AssignmentHandler(CinodeApi cinodeApi) {
        this.cinodeApi = cinodeApi;
    }


    List<AssignmentResponse> getEndingAssignments() {
        List<AssignmentResponse> assignmentResponseList = new ArrayList<>();
        List<Integer> projectsResponseList = getProjectSublistToCheckForAssignments();
        for (Integer projectId : projectsResponseList) {
            ProjectResponse projectResponse = getProject(projectId);
            if (projectResponse == null) {
                log.warn("Got empty response for projectId {}", projectId);
                continue;
            }
            SleepUtil.sleepMilliSeconds(500);
            List<AssignmentResponse> notificationDataListForProject = addAssignmentsToNotificationList(projectResponse.getAssignmentResponses());
            assignmentResponseList.addAll(notificationDataListForProject);
        }
        return assignmentResponseList;
    }

    private List<Integer> getProjectSublistToCheckForAssignments() {
        if (fetchProjects) {
            List<ProjectList> projectListList = cinodeApi.getProjects();
            if (projectListList == null) {
                log.error("No projects returned!");
                return new ArrayList<>();
            }
            fetchProjects = false;
            projectListList.sort(Collections.reverseOrder(Comparator.comparing(ProjectList::getId)));
            for (ProjectList projectResponse : projectListList) {
                projectsToFetch.add(projectResponse.getId());
            }
        }
        int lastIndex = numberOfProjectsToFetch;
        if (projectsToFetch.size() < numberOfProjectsToFetch) {
            lastIndex = projectsToFetch.size();
            fetchProjects = true; //TODO: Add a scheduler to set this?
        }
        List<Integer> subList = new ArrayList<>(projectsToFetch.subList(0, lastIndex));
        projectsToFetch.subList(0, lastIndex).clear();
        log.info("Projects left to fetch {}", projectsToFetch.size());
        return subList;
    }


    private ProjectResponse getProject(int projectId) {
        return cinodeApi.getProject(projectId);
    }


    private List<AssignmentResponse> addAssignmentsToNotificationList(List<AssignmentResponse> assignmentResponseList) {
        if (assignmentResponseList == null) {
            log.warn("Project did not contain assignment!");
            return new ArrayList<>();
        }
        List<AssignmentResponse> notEndingAssignmentsList = new ArrayList<>();
        for (AssignmentResponse assignmentResponse : assignmentResponseList) {
            log.debug("Assignments: {} {} {} {}", assignmentResponse.getTitle(), assignmentResponse.getDescription(), assignmentResponse.getStartDate(), assignmentResponse.getEndDate());
            if (!checkIfAssignmentIsEnding(assignmentResponse)) {
                notEndingAssignmentsList.add(assignmentResponse);
            }
        }
        assignmentResponseList.removeAll(notEndingAssignmentsList);
        return assignmentResponseList;
    }


    private boolean checkIfAssignmentIsEnding(AssignmentResponse assignmentResponse) {
        if (assignmentResponse.getEndDate() == null) {
            return false;
        }
        if (isAssignmentToBeNotifiedBasedOnEndDate(assignmentResponse)) {
            log.info("Found assignmentResponse ending within {} weeks. {}", weeksBeforeAssignmentEndsToNotify, assignmentResponse);
            return true;
        }
        return false;
    }

    private boolean isAssignmentToBeNotifiedBasedOnEndDate(AssignmentResponse assignmentResponse) {
        return assignmentResponse.getEndDate().isBefore(LocalDateTime.now().plusWeeks(weeksBeforeAssignmentEndsToNotify))
                && assignmentResponse.getEndDate().isAfter(LocalDateTime.now().minusWeeks(weeksAfterAssignmentEndsToNotify));
    }


}
