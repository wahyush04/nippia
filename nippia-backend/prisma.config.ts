import "dotenv/config";
import { defineConfig } from "prisma/config";

export default defineConfig({
  schema: "prisma/schema.prisma",
  datasource: {
    url: process.env.DATABASE_URL ?? "postgresql://nippia:nippia_secret@127.0.0.1:5432/nippia_db",
  },
  skills: {
    agents: ["claude", "cursor", "agents", "devin"],
  },
});
