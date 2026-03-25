package data.preferences.impl.core.mapper.base

import common.core.core.mapper.Mapper
import data.core.source.resource.Resource

/**
 * A bidirectional mapper interface for converting between domain models and data transfer objects (DTOs).
 *
 * ```kotlin
 * object UserMapper : ProtobufPreferenceMapper<UserModel, UserDto> {
 *     override val toDomain = Mapper<UserDto, UserModel> { dto ->
 *         UserModel(id = dto.id, email = dto.email)
 *     }
 *
 *     override val toPreference = Mapper<UserModel, UserDto> { model ->
 *         UserDto(id = model.id, email = model.email)
 *     }
 * }
 * ```
 *
 * ```kotlin
 * object OrderMapper : ProtobufPreferenceMapper<Order, OrderDto> {
 *
 *     override val toDomain = Mapper<OrderDto, Order> { dto ->
 *         Order(
 *             id = dto.id,
 *             items = dto.items.map(OrderItemMapper.toDomain::map)
 *         )
 *     }
 *
 *     override val toPreference = Mapper<Order, OrderDto> { model ->
 *         OrderDto(
 *             id = model.id,
 *             items = model.items.map(OrderItemMapper.toPreference::map)
 *         )
 *     }
 * }
 * ```
 */
public interface ProtobufPreferenceMapper<PROTOBUF : Any, PREFERENCE : Resource> {
    public val toProtobuf: Mapper<PREFERENCE, PROTOBUF>
    public val toPreference: Mapper<PROTOBUF, PREFERENCE>
}
