import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.tiendaonline.clientes.Clientes;
import com.tiendaonline.clientes.ClientesService;
import com.tiendaonline.clientes.ClienteDTO;

@RestController
@RequestMapping("/v1/clientes")
public class ClientesController {
    private final ClientesService clientesService;

    public ClientesController(ClientesService clientesService) {
        this.clientesService = clientesService;
    }
    @PostMapping 
    public ResponseEntity<ClienteDTO> guardarCliente(@RequestBody ClienteDTO dto) {
        ClienteDTO clienteGuardado = clientesService.guardarCliente(dto);
        return new ResponseEntity<>(clienteGuardado, HttpStatus.CREATED);
    }
    
}
