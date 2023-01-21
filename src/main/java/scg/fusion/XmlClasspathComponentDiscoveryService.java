package scg.fusion;

public interface XmlClasspathComponentDiscoveryService extends ComponentDiscoveryService {
    @Override
    default void discover(ComponentDiscovery discovery) {
        XmlClasspathDiscoveryDescriptor.accept(discovery);
        discoverAlso(discovery);
    }

    default void discoverAlso(ComponentDiscovery discovery) {};

}
