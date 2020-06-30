package sw2.lab6.teletok.config;

import oracle.jrockit.jfr.openmbean.ProducerDescriptorType;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;


import sw2.lab6.teletok.entity.Token;
import sw2.lab6.teletok.entity.User;
import sw2.lab6.teletok.repository.TokenRepository;
import sw2.lab6.teletok.repository.UserRepository;

import org.springframework.web.multipart.MultipartFile;
import sw2.lab6.teletok.entity.Post;
import sw2.lab6.teletok.entity.PostComment;
import sw2.lab6.teletok.entity.StorageServices;
import sw2.lab6.teletok.entity.Token;
import sw2.lab6.teletok.repository.PostCommentRepository;
import sw2.lab6.teletok.repository.PostRepository;
import sw2.lab6.teletok.repository.TokenRepository;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import java.util.Optional;
import java.util.UUID;

import sw2.lab6.teletok.entity.Post;




import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin
@RequestMapping ("/ws")
public class WebServiceController {


    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenRepository tokenRepository;

    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signin(@RequestBody User u){
        HashMap<String, Object> hashMap = new HashMap<>();
        Optional<User> opt = userRepository.findByUsername(u.getUsername());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String tok = null;
        if(opt.isPresent()){
            User us = opt.get();
            if(us.getPassword().equals(u.getPassword())){
                hashMap.put("status", "AUTHENTICATED");
                hashMap.put("token", "asldkhaslkdalskdj");
                httpStatus = HttpStatus.OK;
                Token token= new Token();
                token.setUser(us);
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

        git
    }

    @Autowired
    PostRepository postRepository;
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
