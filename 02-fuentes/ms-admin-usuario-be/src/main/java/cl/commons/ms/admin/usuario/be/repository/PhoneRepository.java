package cl.commons.ms.admin.usuario.be.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cl.commons.ms.admin.usuario.be.entity.Phone;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long>{
    
    @Query("select p  from Phone p where p.user.id=:userId")
    Optional<List<Phone>> findByIdUser(@Param("userId") UUID idUser);
    
    @Query("select p  from Phone p where p.user.id=:userId and p.id=:id")
    Optional<Phone> findByIdUserIdPhone(@Param("userId") UUID userId,@Param("id")Long id);

    @Query("select p  from Phone p where p.user.id=:userId  and p.number=:number and p.cityCode=:cityCode  and p.contryCode=:contryCode  ")
    Optional<Phone> findByIdUserIdPhoneAndNumber(@Param("userId") UUID userId,@Param("number")Long number,@Param("cityCode")int cityCode,@Param("contryCode")String contryCode);
}
