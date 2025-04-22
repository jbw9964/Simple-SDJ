package group.practice.repositories;

import group.practice.entities.*;
import org.springframework.data.jpa.repository.*;

public interface TeamInfoRepository
        extends JpaRepository<TeamInfo, Long> {

}
