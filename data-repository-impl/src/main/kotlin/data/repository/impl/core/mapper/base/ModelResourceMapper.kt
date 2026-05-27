package data.repository.impl.core.mapper.base

import common.core.core.mapper.Mapper
import data.core.source.resource.Resource
import domain.core.source.model.base.Model

/**
 * A bidirectional mapper interface for converting between domain models and data transfer objects (DTOs).
 *
 * This interface establishes a contract for two-way mapping between the domain layer's [Model] types
 * and the data layer's [Resource] types. Implementations provide both forward ([toModel]) and reverse
 * ([toResource]) mapping capabilities, enabling clean separation of concerns across architectural layers.
 *
 * ```kotlin
 * object UserMapper : ModelResourceMapper<UserModel, UserDto> {
 *     override val toModel = Mapper<UserDto, UserModel> { dto ->
 *         UserModel(id = dto.id, email = dto.email)
 *     }
 *
 *     override val toResource = Mapper<UserModel, UserDto> { model ->
 *         UserDto(id = model.id, email = model.email)
 *     }
 * }
 * ```
 *
 * ```kotlin
 * object OrderMapper : ModelResourceMapper<Order, OrderDto> {
 *
 *     override val toModel = Mapper<OrderDto, Order> { dto ->
 *         Order(
 *             id = dto.id,
 *             items = dto.items.map(OrderItemMapper.toModel::map)
 *         )
 *     }
 *
 *     override val toResource = Mapper<Order, OrderDto> { model ->
 *         OrderDto(
 *             id = model.id,
 *             items = model.items.map(OrderItemMapper.toResource::map)
 *         )
 *     }
 * }
 * ```
 *
 * @param MODEL The domain model type, bounded by [Model].
 * @param RESOURCE The data resource type, bounded by [Resource].
 * @see Mapper
 * @see Model
 * @see Resource
 * @since 0.0.1
 */
public interface ModelResourceMapper<MODEL : Model, RESOURCE : Resource> {
    /**
     * Mapper that converts a [RESOURCE] data transfer object into a [MODEL] domain model.
     */
    public val toModel: Mapper<RESOURCE, MODEL>

    /**
     * Mapper that converts a [MODEL] domain model into a [RESOURCE] data transfer object.
     */
    public val toResource: Mapper<MODEL, RESOURCE>
}
