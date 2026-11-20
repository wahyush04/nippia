import "dotenv/config";
import { defineConfig } from "prisma/config";

export default defineConfig({
  schema: "prisma/schema.prisma",
  datasource: {
    url: process.env.DATABASE_URL ?? "postgresql://nippia:nippia_secret@localhost:5432/nippia_db",
  },
  skills: {
    agents: ["claude", "cursor", "agents", "devin"],
  },
});
