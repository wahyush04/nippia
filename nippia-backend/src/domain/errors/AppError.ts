export interface ErrorDetail {
  field: string;
  issue: string;
}

export class AppError extends Error {
  public readonly statusCode: number;
  public readonly code: string;
  public readonly details?: ErrorDetail[] | undefined;

  constructor(
    statusCode: number,
    code: string,
    message: string,
    details?: ErrorDetail[],
  ) {
    super(message);
    this.name = "AppError";
    this.statusCode = statusCode;
    this.code = code;
    this.details = details;
  }

  static validation(message: string, details?: ErrorDetail[]): AppError {
    return new AppError(400, "VALIDATION_ERROR", message, details);
  }

  static unauthorized(code = "UNAUTHORIZED", message = "Autentikasi dibutuhkan."): AppError {
    return new AppError(401, code, message);
  }

  static forbidden(code = "FORBIDDEN", message = "Akses ditolak."): AppError {
    return new AppError(403, code, message);
  }

  static notFound(message = "Resource tidak ditemukan."): AppError {
    return new AppError(404, "RESOURCE_NOT_FOUND", message);
  }

  static conflict(code: string, message: string): AppError {
    return new AppError(409, code, message);
  }
}
