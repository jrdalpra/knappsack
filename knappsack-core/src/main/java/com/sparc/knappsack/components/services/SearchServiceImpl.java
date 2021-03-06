package com.sparc.knappsack.components.services;

import com.sparc.knappsack.comparators.ApplicationDescriptionComparator;
import com.sparc.knappsack.comparators.ApplicationNameComparator;
import com.sparc.knappsack.components.entities.Application;
import com.sparc.knappsack.components.entities.User;
import com.sparc.knappsack.enums.AppState;
import com.sparc.knappsack.enums.ApplicationType;
import com.sparc.knappsack.models.ApplicationModel;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Transactional( propagation = Propagation.REQUIRED )
@Service("searchService")
public class SearchServiceImpl implements SearchService {

    @Qualifier("applicationService")
    @Autowired(required = true)
    private ApplicationService applicationService;

    @Qualifier("userService")
    @Autowired(required = true)
    private UserService userService;

    @Override
    public List<ApplicationModel> searchApplications(String criteria, User user, ApplicationType deviceType) {
        List<Application> applications = new ArrayList<Application>();
        if (criteria != null && !criteria.isEmpty()) {
            criteria = criteria.toLowerCase();
            List<Application> userApplications = userService.getApplicationsForUser(user, deviceType, AppState.ORGANIZATION_PUBLISH, AppState.GROUP_PUBLISH, AppState.ORG_PUBLISH_REQUEST);
            for (Application application : userApplications) {
                if ((application.getName().toLowerCase().contains(criteria) || application.getDescription().toLowerCase().contains(criteria)) && !applications.contains(application)) {
                    applications.add(application);
                }
            }
            ComparatorChain chain = new ComparatorChain();
            chain.addComparator(new ApplicationNameComparator());
            chain.addComparator(new ApplicationDescriptionComparator());

            Collections.sort(applications, chain);
        }

        return applicationService.createApplicationModels(applications);
    }

}
