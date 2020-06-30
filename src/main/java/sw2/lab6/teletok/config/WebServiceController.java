package sw2.lab6.teletok.config;

import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sw2.lab6.teletok.entity.Post;
import sw2.lab6.teletok.entity.StorageServices;
import sw2.lab6.teletok.entity.Token;
import sw2.lab6.teletok.repository.PostRepository;
import sw2.lab6.teletok.repository.TokenRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class WebServiceController {

    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    PostRepository postRepository;

    @PostMapping(value = "/product", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity GuardarPost(
            @RequestBody String token, @RequestBody String description, @RequestBody MultipartFile file) {

        HashMap<String, Object> responseMap = new HashMap<>();
        StorageServices storageServices = new StorageServices();
        HashMap<String, String> map = storageServices.store(file);
        Optional<Token> optionalToken = tokenRepository.findByCode(token);
        if(optionalToken.isPresent()){
            if(map.get("estado").equalsIgnoreCase("exito")){
                 Post post=new Post();
                 post.setMediaUrl(map.get("fileName"));
                 post.setDescription(description);
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity gestionExcepcion(HttpServletRequest request) {

        HashMap<String, Object> responseMap = new HashMap<>();
        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
            responseMap.put("error", "UPLOAD_ERROR");
        }
        return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
    }

}
