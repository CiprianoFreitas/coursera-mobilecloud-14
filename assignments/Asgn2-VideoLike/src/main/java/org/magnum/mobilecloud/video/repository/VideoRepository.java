package org.magnum.mobilecloud.video.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Created by cipri on 24/08/2014.
 */
@Repository
public interface VideoRepository extends CrudRepository<Video, Long> {
    // Find all videos with a matching title (e.g., Video.name)
    public Collection<Video> findByName(String title);

    public Collection<Video> findByDurationLessThan(long duration);
}
