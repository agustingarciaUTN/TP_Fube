package com.tiendaonline.pedidos.controlador;

import com.tiendaonline.pedidos.dto.CambiarEstadoRequest;
import com.tiendaonline.pedidos.dto.CrearPedidoRequest;
import com.tiendaonline.pedidos.dto.ListaPedidosResponse;
import com.tiendaonline.pedidos.dto.PedidoResponse;
import com.tiendaonline.pedidos.servicio.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;

    @GetMapping("/{id}")
    public PedidoResponse buscarPedidoPorId(@PathVariable UUID id) {
        return pedidoService.obtener(id);
    }

    @GetMapping
    public ListaPedidosResponse listarPedidosPorCliente(@RequestParam UUID clienteId) {
        return pedidoService.listarPorCliente(clienteId);
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody CrearPedidoRequest request){

        PedidoResponse pedido =pedidoService.crear(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pedido.id())
                .toUri();

        return ResponseEntity.created(uri).body(pedido);
    }

    @PatchMapping("/{id}")
    public PedidoResponse cambiarEstado(@PathVariable UUID id,
                                        @Valid @RequestBody CambiarEstadoRequest request){
        return pedidoService.cambiarEstado(id, request.estado());
    }
}