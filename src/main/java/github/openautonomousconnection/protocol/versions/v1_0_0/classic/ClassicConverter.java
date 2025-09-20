package github.openautonomousconnection.protocol.versions.v1_0_0.classic;

import github.openautonomousconnection.protocol.versions.v1_0_0.beta.Domain;

public class ClassicConverter {

    public static Domain classicDomainToNewDomain(Classic_Domain classicDomain) {
        return new Domain(classicDomain.name + "." + classicDomain.topLevelDomain + (classicDomain.path.startsWith("/") ? classicDomain.path : "/" + classicDomain.path));
    }

    public static Classic_Domain newDomainToClassicDomain(Domain newDomain) {
        return new Classic_Domain(newDomain.getName(), newDomain.getTopLevelName(), newDomain.getDestination(), newDomain.getPath());
    }

}
