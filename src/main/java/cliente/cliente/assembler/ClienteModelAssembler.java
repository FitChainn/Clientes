package cliente.cliente.assembler;

import cliente.cliente.Controller.ClienteController;
import cliente.cliente.dto.ClienteResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClienteModelAssembler implements RepresentationModelAssembler<ClienteResponseDTO, EntityModel<ClienteResponseDTO>> {
    @Override
    public EntityModel<ClienteResponseDTO> toModel(ClienteResponseDTO dto) {
        return EntityModel.of(
                dto,
                linkTo(methodOn(ClienteController.class).obtenerCliente(dto.getId())).withSelfRel(),
                linkTo(methodOn(ClienteController.class).eliminar(dto.getId())).withRel("delete")
        );
    }
}
