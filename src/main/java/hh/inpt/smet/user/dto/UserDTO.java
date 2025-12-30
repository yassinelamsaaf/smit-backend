package hh.inpt.smet.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class UserDTO {

	@Data
	@AllArgsConstructor
	@Builder
	public static class PostInput {

		private String username;
		private String email;
		private String password;

	}
	
	@Data
    @AllArgsConstructor
    @Builder
	public static class PostOutput {
		private Long id;
		private String username;
		private String email;
	}

}
