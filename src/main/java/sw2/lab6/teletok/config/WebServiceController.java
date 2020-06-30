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
import oracle.jrockit.jfr.openmbean.ProducerDescriptorType;
import javafx.geometry.Pos;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sw2.lab6.teletok.entity.Token;
import sw2.lab6.teletok.entity.User;
import sw2.lab6.teletok.repository.TokenRepository;
import sw2.lab6.teletok.repository.UserRepository;
import org.springframework.web.multipart.MultipartFile;
import sw2.lab6.teletok.entity.PostComment;
import sw2.lab6.teletok.entity.StorageServices;
import sw2.lab6.teletok.repository.PostCommentRepository;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping ("/ws")
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


    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenRepository tokenRepository;

    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signin(@RequestBody User u){
        HashMap<String, Object> hashMap = new HashMap<>();
        Optional<User> opt = userRepository.findByUsername(u.getUsername());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean match = passwordEncoder.matches(opt.get().getPassword(), u.getPassword());
        String tok = null;
        if(opt.isPresent()){
            if(match){
                hashMap.put("status", "AUTHENTICATED");
                hashMap.put("token", "asldkhaslkdalskdj");
                httpStatus = HttpStatus.OK;
                Token token= new Token();
                token.setUser(opt.get());
                token.setCode(tok);
                tokenRepository.save(token);
            }else{
                hashMap.put("error", "AUTH_FAILED");
            }
        }else{
            hashMap.put("error", "AUTH_FAILED");
        }
        return new ResponseEntity(hashMap, httpStatus);
    }

    @PostMapping(value = "/like/{token}/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity like(@PathVariable("token") String tok, @PathVariable("postId") int postId){
        HashMap<String, Object> hashMap = new HashMap<>();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Optional<Post> opt2 = postRepository.findById(postId);
        Optional<Token> opt = tokenRepository.findByCode(tok);
        if(opt.isPresent()){
            if(opt2.isPresent()){
                hashMap.put("likeId", opt2.get().getId());
                hashMap.put("status", "LIKE_CREATED");
                httpStatus = HttpStatus.OK;
            }else{
                hashMap.put("error", "POST_NOT_FOUND");
            }
        }else{
            hashMap.put("error", "TOKEN_INVALID");
        }
        return new ResponseEntity(hashMap, httpStatus);
    }

    @Autowired
    PostCommentRepository postCommentRepository;


    @PostMapping(value = "/post/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity crearPost(
            @RequestBody String token, @RequestBody String description, @RequestBody MultipartFile media) {


        HashMap<String, Object> responseMap = new HashMap<>();
        StorageServices storageServices = new StorageServices();
        HashMap<String, String> map = storageServices.store(media);
        Optional<Token> optionalToken = tokenRepository.findByCode(token);
        if(optionalToken.isPresent()){
            if(map.get("estado").equalsIgnoreCase("exito")){
                 Post post=new Post();
                 post.setMediaUrl(map.get("fileName"));
                 post.setDescription(description);
                 post.setUser(optionalToken.get().getUser());
                 postRepository.save(post);
                List<Post> posts = postRepository.findAll();
                responseMap.put("postId", posts.get(posts.size()-1));
                responseMap.put("status", "POST_CREATED");
            }else{
                responseMap.put("error", map.get("msg"));
            }
        }else{
            responseMap.put("error", "TOKEN_INVALID");
        }
        return new ResponseEntity(responseMap, HttpStatus.CREATED);
    }

    @PostMapping(value = "/post/comment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity GuardarComentario(
                @RequestBody String token, @RequestBody int postId, @RequestBody String message) {

        HashMap<String, Object> responseMap = new HashMap<>();
        Optional<Token> optionalToken = tokenRepository.findByCode(token);
        if(optionalToken.isPresent()){
            Optional<Post> postOptional = postRepository.findById(postId);
            if(postOptional.isPresent()){
                PostComment postComment = new PostComment();
                postComment.setMessage(message);
                postComment.setPost(postOptional.get());
                postComment.setUser(optionalToken.get().getUser());
                List<PostComment> postComments = postCommentRepository.findAll();
                responseMap.put("commentId", postComments.get(postComments.size()-1));
                responseMap.put("status", "COMMENT_CREATED");
            }else {
                responseMap.put("error", "POST_NOT_FOUND");
            }
        }else{
            responseMap.put("error", "TOKEN_INVALID");
        }
        return new ResponseEntity(responseMap, HttpStatus.CREATED);
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity gestionExcepcion(HttpServletRequest request) {

        HashMap<String, Object> responseMap = new HashMap<>();
        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
            responseMap.put("error", "UPLOAD_ERROR");
        }
        return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
    }

}
