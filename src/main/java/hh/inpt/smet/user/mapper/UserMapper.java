package hh.inpt.smet.user.mapper;

import hh.inpt.smet.user.dto.UserDTO;
import hh.inpt.smet.user.model.UserEntity;

public class UserMapper {

	private UserMapper() {
	}

	// DTO → Entity (POST / PUT)
	public static UserEntity toEntity(UserDTO.PostInput dto) {

		if (dto == null) {
			return null;
		}

		return UserEntity.builder().username(dto.getUsername()).email(dto.getEmail()).password(dto.getPassword()).build();
	}

	// Entity → DTO (GET / POST response)
	public static UserDTO.PostOutput toOutput(UserEntity entity) {
		if (entity == null) {
			return null;
		}

		return UserDTO.PostOutput.builder().id(entity.getId()).username(entity.getUsername()).email(entity.getEmail())
			.build();
	}
}