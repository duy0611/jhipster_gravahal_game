package fi.vaasa.dnguyen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.vaasa.dnguyen.domain.SideBoard;

public interface SideBoardRepository extends JpaRepository<SideBoard, Long> {
	
}
