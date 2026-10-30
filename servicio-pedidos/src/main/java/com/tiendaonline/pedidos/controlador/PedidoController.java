package com.tiendaonline.pedidos.controlador;

import com.tiendaonline.pedidos.dto.ListaPedidosResponse;
import com.tiendaonline.pedidos.dto.PedidoResponse;
import com.tiendaonline.pedidos.servicio.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}