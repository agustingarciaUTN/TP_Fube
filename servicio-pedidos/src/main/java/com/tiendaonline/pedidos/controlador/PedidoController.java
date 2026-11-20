package com.tiendaonline.pedidos.controlador;

import com.tiendaonline.pedidos.dto.CambiarEstadoRequest;
import com.tiendaonline.pedidos.dto.CrearPedidoRequest;
import com.tiendaonline.pedidos.dto.ListaPedidosResponse;
import com.tiendaonline.pedidos.dto.PedidoResponse;
import com.tiendaonline.pedidos.servicio.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/v1/pedidos")
@RequiredArgsConstructor
@Tag(
        name = "Pedidos",
        description = "Alta, consulta, listado y cambio de estado de pedidos." +
                " Al crear, verifica el cliente y reserva stock en inventario."
)
public class PedidoController {
    private final PedidoService pedidoService;

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un pedido", description = "Devuelve el pedido con sus items, estado y monto total")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "400", description = "El id no es un UUID valido", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "No existe un pedido con ese id", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public PedidoResponse buscarPedidoPorId(@Parameter(description = "Id del pedido", example = "be9b19c4-02cc-488e-b53b-927670ef29c3")
                                                @PathVariable UUID id) {
        return pedidoService.obtener(id);
    }

    @GetMapping
    @Operation(summary = "Listar los pedidos de un cliente",
            description = "Devuelve los pedidos del cliente indicado y el total. " +
                    "Si no tiene pedidos, la lista viene vacia. Lo consume el servicio de clientes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de pedidos del cliente"),
            @ApiResponse(responseCode = "400", description = "Falta clienteId o no es un UUID valido", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })

    public ListaPedidosResponse listarPedidosPorCliente(@Parameter(description = "Id del cliente", example = "ad10043d-56ca-43fb-8799-e7ca95eb7018")
                                                            @RequestParam UUID clienteId) {
        return pedidoService.listarPorCliente(clienteId);
    }

    @PostMapping
    @Operation(summary = "Crear pedido",
            description = "Verifica que el cliente exista, reserva stock de cada item en inventario y calcula el monto total. " +
                    "Si falla una reserva, libera las anteriores y el pedido no se crea. Nace en estado CREADO")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado, el header Location apunta al nuevo recurso"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos: falta el cliente, no hay items o una cantidad no es positiva", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "No hay stock suficiente de algun producto", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "El cliente o algun producto no existe", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "503", description = "Los servicios clientes o inventario no estan disponibles", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody CrearPedidoRequest request){

        PedidoResponse pedido =pedidoService.crear(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pedido.id())
                .toUri();

        return ResponseEntity.created(uri).body(pedido);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Cambiar el estado de un pedido",
            description = "Confirma o cancela un pedido. Cancelar se puede desde CREADO o CONFIRMADO y libera las reservas en inventario. " +
                    "EXPIRADO no se acepta, lo asigna solo el proceso nocturno.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "400", description = "El estado enviado no existe", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "No existe un pedido con ese id", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Transicion de estado invalida", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "503", description = "El servicio de inventario no esta disponible para liberar las reservas, el estado no cambia", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public PedidoResponse cambiarEstado(@Parameter(description = "Id del pedido", example = "be9b19c4-02cc-488e-b53b-927670ef29c3")
                                            @PathVariable UUID id,
                                        @Valid @RequestBody CambiarEstadoRequest request){
        return pedidoService.cambiarEstado(id, request.estado());
    }
}