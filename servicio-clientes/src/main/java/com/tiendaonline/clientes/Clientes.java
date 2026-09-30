import java.sql.Timestamp;
@Entity
@Table(name = "clientes")
@AllArgsConstructor
@NoArgsConstructor
public class Clientes {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)    
private Long id;
private String nombre;
private String email;
private Timestamp fechaAlta;

}