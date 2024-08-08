package com.ghtk.auction.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
	Boolean success;
	String message;
	T data;

	public static <D> ApiResponse<D> success(D data) {
		ApiResponse<D> response = new ApiResponse<>();
		response.success = Boolean.TRUE;
		response.message = "Thành công";
		response.data = data;
		return response;
	}

	public static <D> ApiResponse<D> ok(String message) {
		ApiResponse<D> response = new ApiResponse<>();
		response.success = Boolean.TRUE;
		response.message = message;
		response.data = null;
		return response;
	}

	public static <D> ApiResponse<D> error(String message, D data) {
		ApiResponse<D> response = new ApiResponse<>();
		response.success = Boolean.FALSE;
		response.message = message;
		response.data = data;
		return response;
	}

	public static ApiResponse<Void> error(String message) {
		return error(message, null);
	}

	public static ApiResponse<Void> success(String message) {
		return ok(message);
	}
}