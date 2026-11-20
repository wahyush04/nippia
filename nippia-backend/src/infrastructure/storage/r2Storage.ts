import { PutObjectCommand, S3Client } from "@aws-sdk/client-s3";
import sharp from "sharp";
import { env } from "../config/env.js";

let s3Client: S3Client | null = null;

function getS3Client(): S3Client {
  if (!env.R2_ENDPOINT || !env.R2_ACCESS_KEY_ID || !env.R2_SECRET_ACCESS_KEY) {
    throw new Error("Cloudflare R2 / S3 credentials are not configured.");
  }
  if (!s3Client) {
    s3Client = new S3Client({
      region: "auto",
      endpoint: env.R2_ENDPOINT,
      credentials: {
        accessKeyId: env.R2_ACCESS_KEY_ID,
        secretAccessKey: env.R2_SECRET_ACCESS_KEY,
      },
    });
  }
  return s3Client;
}

/**
 * Converts an input image buffer to optimized WebP and uploads it to Cloudflare R2 / S3.
 */
export async function uploadWebpImage(
  buffer: Buffer,
  objectKey: string,
): Promise<string> {
  const webpBuffer = await sharp(buffer)
    .webp({ quality: 85 })
    .toBuffer();

  const normalizedKey = objectKey.endsWith(".webp") ? objectKey : `${objectKey}.webp`;

  await getS3Client().send(
    new PutObjectCommand({
      Bucket: env.R2_BUCKET,
      Key: normalizedKey,
      Body: webpBuffer,
      ContentType: "image/webp",
      CacheControl: "public, max-age=31536000, immutable",
    }),
  );

  const baseUrl = env.R2_PUBLIC_URL.replace(/\/$/, "");
  return baseUrl ? `${baseUrl}/${normalizedKey}` : normalizedKey;
}
