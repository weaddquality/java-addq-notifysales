package se.addq.notifysales.cinode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.addq.notifysales.cinode.model.ProjectList;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


//Class for filtering and batching calls on project API
@Component
public class ProjectHandler {

    private boolean fetchProjects = true;

    @Value("${cinode.projects.group.size}")
    private int numberOfProjectsToFetch;

    private final List<Integer> projectsToFetch = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CinodeApi cinodeApi;

    void setNumberOfProjectsToFetch(int numberOfProjectsToFetch) {
        this.numberOfProjectsToFetch = numberOfProjectsToFetch;
    }

    @Autowired
    ProjectHandler(CinodeApi cinodeApi) {
        this.cinodeApi = cinodeApi;
    }

    public List<Integer> getProjectSublistToCheckForAssignments() {
        if (fetchProjects) {
            List<ProjectList> projectListList = cinodeApi.getProjects();
            fetchProjects = false;
            projectListList.sort(Collections.reverseOrder(Comparator.comparing(ProjectList::getId)));
            for (ProjectList projectResponse : projectListList) {
                projectsToFetch.add(projectResponse.getId());
            }
        }
        int lastIndex = numberOfProjectsToFetch;
        if (projectsToFetch.size() < numberOfProjectsToFetch) {
            lastIndex = projectsToFetch.size();
            fetchProjects = true;
        }
        List<Integer> subList = new ArrayList<>(projectsToFetch.subList(0, lastIndex));
        projectsToFetch.subList(0, lastIndex).clear();
        log.info("Projects left to fetch {}", projectsToFetch.size());
        return subList;
    }


}
