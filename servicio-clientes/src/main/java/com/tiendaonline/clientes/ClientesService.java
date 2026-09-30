import com.tiendaonline.clientes.ClientesRepository;
@Service
@NoArgsConstructor
@AllArgsConstructor
public class ClientesService {
    private final ClientesRepository clientesRepository;

    public ClientesService(ClientesRepository clientesRepository) {
        this.clientesRepository = clientesRepository;
    }   
public ClienteDTO guardarCliente(ClienteDTO dto) {
        Clientes cliente = new Clientes();
        cliente.setNombre(dto.getNombre());
        cliente.setEmail(dto.getEmail());
        cliente.setFechaAlta(new Timestamp(System.currentTimeMillis()));
        Clientes clienteGuardado = clientesRepository.save(cliente);
        return new ClienteDTO(clienteGuardado.getNombre(), clienteGuardado.getEmail());
    }

    
}
