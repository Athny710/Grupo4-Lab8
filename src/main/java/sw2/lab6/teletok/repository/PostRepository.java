package sw2.lab6.teletok.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sw2.lab6.teletok.dto.ListaPosts;
import sw2.lab6.teletok.entity.Post;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "select p.description as descripcion, p.media_url as foto, count(pl.id) as megusta, count(pc.message) as comentarios, us.fullname as nombrec, hour(now()) - (p.creation_date) as hora, minute(now()) - (p.creation_date) as minuto, second(now()) - (p.creation_date) as segundo from post p\n" +
            "inner join post_comment pc on p.id = pc.post_id\n" +
            "inner join user uspm on pc.user_id = uspm.id\n" +
            "inner join user us on p.user_id = us.id\n" +
            "inner join post_like pl on p.id = pl.post_id\n" +
            "inner join user uspl on pl.user_id = uspl.id\n" +
            "order by p.creation_date desc", nativeQuery = true)
    List<ListaPosts> obtenerListaPosts();

    @Query(value = "select p.description as descripcion, p.media_url as foto, count(pl.id) as megusta, count(pc.message) as comentarios, us.fullname as nombrec, hour(now()) - (p.creation_date) as hora, minute(now()) - (p.creation_date) as minuto, second(now()) - (p.creation_date) as segundo from post p\n" +
            "inner join post_comment pc on p.id = pc.post_id\n" +
            "inner join user uspm on pc.user_id = uspm.id\n" +
            "inner join user us on p.user_id = us.id\n" +
            "inner join post_like pl on p.id = pl.post_id\n" +
            "inner join user uspl on pl.user_id = uspl.id and p.description = ?1 or us.fullname = ?2\n" +
            "order by p.creation_date desc", nativeQuery = true)
    List<ListaPosts> obtenerPorBusqueda(String descripcion, String nombre);



}
