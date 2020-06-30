package sw2.lab6.teletok.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sw2.lab6.teletok.dto.DetallePost;
import sw2.lab6.teletok.dto.ListaPosts;
import sw2.lab6.teletok.entity.Post;
import sw2.lab6.teletok.repository.PostRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class WebServiceController {

    @Autowired
    PostRepository postRepository;

    //localhost:8080/teletok/ws/post/list
    @GetMapping(value = "/ws/post/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity obtenerPosts(@RequestParam("query") String query) {
        HashMap<String, Object> responseMap = new HashMap<>();

        List<ListaPosts> listaPosts;
        if (query.equals("")){
            listaPosts = postRepository.obtenerTodos();
        }else {
            listaPosts = postRepository.obtenerPorBusqueda1(query);
        }

        if (listaPosts.size() >= 1) {
            responseMap.put("estado", "ok");
            responseMap.put("post", listaPosts);
            return new ResponseEntity(responseMap, HttpStatus.OK);
        } else {
            responseMap.put("estado", "error");
            responseMap.put("msg", "no se encontró el post con descripcion o usuario: " + query);
            return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(value = "/ws/post/list?/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity obtenerPersonaje(@PathVariable("id") String id,@RequestParam("token") String token) {
        HashMap<String, Object> responseMap = new HashMap<>();

        List<ListaPosts> listaPosts = postRepository.obtenerPostFiltrado(id);
        List<DetallePost> listaDetalle = postRepository.obtenerDetalle(id);

        if (listaPosts.size() >= 1) {
            responseMap.put("estado", "ok");
            responseMap.put("post", listaPosts);
            responseMap.put("detalle", listaDetalle);
            return new ResponseEntity(responseMap, HttpStatus.OK);
        } else {
            responseMap.put("estado", "error");
            responseMap.put("msg", "no se encontró el post con el id: " + id);
            return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
        }
    }
}
