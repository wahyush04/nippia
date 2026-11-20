-- CreateEnum
CREATE TYPE "UserRole" AS ENUM ('USER', 'ADMIN');

-- CreateEnum
CREATE TYPE "DailyGoal" AS ENUM ('SANTAI', 'REGULER', 'AMBISIUS');

-- CreateEnum
CREATE TYPE "CategoryCode" AS ENUM ('TWK', 'TIU', 'TKP');

-- CreateEnum
CREATE TYPE "ContentStatus" AS ENUM ('DRAFT', 'PUBLISHED', 'ARCHIVED');

-- CreateEnum
CREATE TYPE "QuestionType" AS ENUM ('MULTIPLE_CHOICE', 'WEIGHTED_SCALE');

-- CreateEnum
CREATE TYPE "OptionLabel" AS ENUM ('A', 'B', 'C', 'D', 'E');

-- CreateEnum
CREATE TYPE "UnitProgressStatus" AS ENUM ('LOCKED', 'ACTIVE', 'COMPLETED');

-- CreateEnum
CREATE TYPE "TryoutType" AS ENUM ('MINI_CAT', 'FULL_CAT');

-- CreateEnum
CREATE TYPE "SubscriptionStatus" AS ENUM ('ACTIVE', 'EXPIRED', 'GRACE_PERIOD', 'CANCELED');

-- CreateEnum
CREATE TYPE "LeagueTier" AS ENUM ('BRONZE', 'SILVER', 'GOLD', 'SAPPHIRE', 'DIAMOND');

-- CreateTable
CREATE TABLE "users" (
    "id" UUID NOT NULL,
    "email" TEXT,
    "google_id" TEXT,
    "display_name" TEXT,
    "avatar_url" TEXT,
    "role" "UserRole" NOT NULL DEFAULT 'USER',
    "is_guest" BOOLEAN NOT NULL DEFAULT false,
    "target_institution" TEXT,
    "target_position" TEXT,
    "daily_goal" "DailyGoal" NOT NULL DEFAULT 'REGULER',
    "daily_xp_target" INTEGER NOT NULL DEFAULT 50,
    "daily_reminder_enabled" BOOLEAN NOT NULL DEFAULT true,
    "daily_reminder_time" TEXT NOT NULL DEFAULT '19:30',
    "onboarding_completed" BOOLEAN NOT NULL DEFAULT false,
    "fcm_token" TEXT,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "users_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "refresh_tokens" (
    "id" UUID NOT NULL,
    "user_id" UUID NOT NULL,
    "token_hash" TEXT NOT NULL,
    "expires_at" TIMESTAMP(3) NOT NULL,
    "revoked_at" TIMESTAMP(3),
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "refresh_tokens_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "categories" (
    "id" UUID NOT NULL,
    "code" "CategoryCode" NOT NULL,
    "name" TEXT NOT NULL,
    "passing_grade" INTEGER NOT NULL,
    "updated_at" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "categories_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "units" (
    "id" UUID NOT NULL,
    "category_id" UUID NOT NULL,
    "section_title" TEXT NOT NULL,
    "order_index" INTEGER NOT NULL,
    "title" TEXT NOT NULL,
    "sub_topic" TEXT,
    "guide_markdown" TEXT NOT NULL,
    "xp_reward" INTEGER NOT NULL DEFAULT 20,
    "is_pro_only" BOOLEAN NOT NULL DEFAULT false,
    "status" "ContentStatus" NOT NULL DEFAULT 'DRAFT',
    "version" INTEGER NOT NULL DEFAULT 1,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "units_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "questions" (
    "id" UUID NOT NULL,
    "unit_id" UUID,
    "category_code" "CategoryCode" NOT NULL,
    "sub_topic" TEXT NOT NULL,
    "type" "QuestionType" NOT NULL,
    "order_index" INTEGER NOT NULL DEFAULT 0,
    "content_text" TEXT NOT NULL,
    "image_url" TEXT,
    "explanation" TEXT NOT NULL,
    "status" "ContentStatus" NOT NULL DEFAULT 'DRAFT',
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "questions_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "question_options" (
    "id" UUID NOT NULL,
    "question_id" UUID NOT NULL,
    "label" "OptionLabel" NOT NULL,
    "option_text" TEXT NOT NULL,
    "image_url" TEXT,
    "score" INTEGER NOT NULL,

    CONSTRAINT "question_options_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "user_unit_progress" (
    "id" UUID NOT NULL,
    "user_id" UUID NOT NULL,
    "unit_id" UUID NOT NULL,
    "status" "UnitProgressStatus" NOT NULL DEFAULT 'LOCKED',
    "best_score" INTEGER NOT NULL DEFAULT 0,
    "attempts_count" INTEGER NOT NULL DEFAULT 0,
    "completed_at" TIMESTAMP(3),
    "updated_at" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "user_unit_progress_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "user_mistakes" (
    "id" UUID NOT NULL,
    "user_id" UUID NOT NULL,
    "question_id" UUID NOT NULL,
    "wrong_count" INTEGER NOT NULL DEFAULT 1,
    "is_resolved" BOOLEAN NOT NULL DEFAULT false,
    "updated_at" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "user_mistakes_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "user_gamification" (
    "user_id" UUID NOT NULL,
    "total_xp" INTEGER NOT NULL DEFAULT 0,
    "weekly_xp" INTEGER NOT NULL DEFAULT 0,
    "gems" INTEGER NOT NULL DEFAULT 0,
    "current_streak" INTEGER NOT NULL DEFAULT 0,
    "longest_streak" INTEGER NOT NULL DEFAULT 0,
    "streak_freezes" INTEGER NOT NULL DEFAULT 0,
    "hearts" INTEGER NOT NULL DEFAULT 5,
    "last_heart_regen_at" TIMESTAMP(3),
    "last_active_date" DATE,
    "league_tier" "LeagueTier" NOT NULL DEFAULT 'BRONZE',
    "updated_at" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "user_gamification_pkey" PRIMARY KEY ("user_id")
);

-- CreateTable
CREATE TABLE "idempotency_records" (
    "id" UUID NOT NULL,
    "user_id" UUID NOT NULL,
    "endpoint" TEXT NOT NULL,
    "idempotency_key" TEXT NOT NULL,
    "response_status" INTEGER NOT NULL,
    "response_body" JSONB NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "idempotency_records_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "subscriptions" (
    "id" UUID NOT NULL,
    "user_id" UUID NOT NULL,
    "product_id" TEXT NOT NULL,
    "purchase_token" TEXT NOT NULL,
    "status" "SubscriptionStatus" NOT NULL,
    "is_trial" BOOLEAN NOT NULL DEFAULT false,
    "expiry_time" TIMESTAMP(3) NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "subscriptions_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "tryout_packages" (
    "id" UUID NOT NULL,
    "title" TEXT NOT NULL,
    "description" TEXT,
    "type" "TryoutType" NOT NULL,
    "duration_minutes" INTEGER NOT NULL,
    "total_questions" INTEGER NOT NULL,
    "is_pro_only" BOOLEAN NOT NULL DEFAULT false,
    "status" "ContentStatus" NOT NULL DEFAULT 'DRAFT',
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "tryout_packages_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "tryout_package_questions" (
    "id" UUID NOT NULL,
    "package_id" UUID NOT NULL,
    "question_id" UUID NOT NULL,
    "order_index" INTEGER NOT NULL,

    CONSTRAINT "tryout_package_questions_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "tryout_attempts" (
    "id" UUID NOT NULL,
    "user_id" UUID NOT NULL,
    "package_id" UUID NOT NULL,
    "started_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "expires_at" TIMESTAMP(3) NOT NULL,
    "submitted_at" TIMESTAMP(3),
    "twk_score" INTEGER NOT NULL DEFAULT 0,
    "tiu_score" INTEGER NOT NULL DEFAULT 0,
    "tkp_score" INTEGER NOT NULL DEFAULT 0,
    "total_score" INTEGER NOT NULL DEFAULT 0,
    "is_passed" BOOLEAN NOT NULL DEFAULT false,
    "answers_json" JSONB,
    "updated_at" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "tryout_attempts_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "users_email_key" ON "users"("email");

-- CreateIndex
CREATE UNIQUE INDEX "users_google_id_key" ON "users"("google_id");

-- CreateIndex
CREATE INDEX "users_updated_at_idx" ON "users"("updated_at");

-- CreateIndex
CREATE UNIQUE INDEX "refresh_tokens_token_hash_key" ON "refresh_tokens"("token_hash");

-- CreateIndex
CREATE INDEX "refresh_tokens_user_id_idx" ON "refresh_tokens"("user_id");

-- CreateIndex
CREATE UNIQUE INDEX "categories_code_key" ON "categories"("code");

-- CreateIndex
CREATE INDEX "categories_updated_at_idx" ON "categories"("updated_at");

-- CreateIndex
CREATE INDEX "units_category_id_status_idx" ON "units"("category_id", "status");

-- CreateIndex
CREATE INDEX "units_updated_at_idx" ON "units"("updated_at");

-- CreateIndex
CREATE UNIQUE INDEX "units_category_id_order_index_key" ON "units"("category_id", "order_index");

-- CreateIndex
CREATE INDEX "questions_unit_id_status_idx" ON "questions"("unit_id", "status");

-- CreateIndex
CREATE INDEX "questions_category_code_sub_topic_idx" ON "questions"("category_code", "sub_topic");

-- CreateIndex
CREATE INDEX "questions_updated_at_idx" ON "questions"("updated_at");

-- CreateIndex
CREATE INDEX "question_options_question_id_idx" ON "question_options"("question_id");

-- CreateIndex
CREATE UNIQUE INDEX "question_options_question_id_label_key" ON "question_options"("question_id", "label");

-- CreateIndex
CREATE INDEX "user_unit_progress_user_id_idx" ON "user_unit_progress"("user_id");

-- CreateIndex
CREATE INDEX "user_unit_progress_updated_at_idx" ON "user_unit_progress"("updated_at");

-- CreateIndex
CREATE UNIQUE INDEX "user_unit_progress_user_id_unit_id_key" ON "user_unit_progress"("user_id", "unit_id");

-- CreateIndex
CREATE INDEX "user_mistakes_user_id_idx" ON "user_mistakes"("user_id");

-- CreateIndex
CREATE INDEX "user_mistakes_user_id_is_resolved_idx" ON "user_mistakes"("user_id", "is_resolved");

-- CreateIndex
CREATE INDEX "user_mistakes_updated_at_idx" ON "user_mistakes"("updated_at");

-- CreateIndex
CREATE UNIQUE INDEX "user_mistakes_user_id_question_id_key" ON "user_mistakes"("user_id", "question_id");

-- CreateIndex
CREATE INDEX "user_gamification_user_id_idx" ON "user_gamification"("user_id");

-- CreateIndex
CREATE INDEX "user_gamification_updated_at_idx" ON "user_gamification"("updated_at");

-- CreateIndex
CREATE INDEX "idempotency_records_user_id_idx" ON "idempotency_records"("user_id");

-- CreateIndex
CREATE UNIQUE INDEX "idempotency_records_user_id_endpoint_idempotency_key_key" ON "idempotency_records"("user_id", "endpoint", "idempotency_key");

-- CreateIndex
CREATE UNIQUE INDEX "subscriptions_purchase_token_key" ON "subscriptions"("purchase_token");

-- CreateIndex
CREATE INDEX "subscriptions_user_id_idx" ON "subscriptions"("user_id");

-- CreateIndex
CREATE INDEX "subscriptions_updated_at_idx" ON "subscriptions"("updated_at");

-- CreateIndex
CREATE INDEX "tryout_packages_status_idx" ON "tryout_packages"("status");

-- CreateIndex
CREATE INDEX "tryout_packages_updated_at_idx" ON "tryout_packages"("updated_at");

-- CreateIndex
CREATE INDEX "tryout_package_questions_package_id_idx" ON "tryout_package_questions"("package_id");

-- CreateIndex
CREATE UNIQUE INDEX "tryout_package_questions_package_id_question_id_key" ON "tryout_package_questions"("package_id", "question_id");

-- CreateIndex
CREATE UNIQUE INDEX "tryout_package_questions_package_id_order_index_key" ON "tryout_package_questions"("package_id", "order_index");

-- CreateIndex
CREATE INDEX "tryout_attempts_user_id_idx" ON "tryout_attempts"("user_id");

-- CreateIndex
CREATE INDEX "tryout_attempts_package_id_idx" ON "tryout_attempts"("package_id");

-- CreateIndex
CREATE INDEX "tryout_attempts_updated_at_idx" ON "tryout_attempts"("updated_at");

-- AddForeignKey
ALTER TABLE "refresh_tokens" ADD CONSTRAINT "refresh_tokens_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "units" ADD CONSTRAINT "units_category_id_fkey" FOREIGN KEY ("category_id") REFERENCES "categories"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "questions" ADD CONSTRAINT "questions_unit_id_fkey" FOREIGN KEY ("unit_id") REFERENCES "units"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "question_options" ADD CONSTRAINT "question_options_question_id_fkey" FOREIGN KEY ("question_id") REFERENCES "questions"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "user_unit_progress" ADD CONSTRAINT "user_unit_progress_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "user_unit_progress" ADD CONSTRAINT "user_unit_progress_unit_id_fkey" FOREIGN KEY ("unit_id") REFERENCES "units"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "user_mistakes" ADD CONSTRAINT "user_mistakes_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "user_mistakes" ADD CONSTRAINT "user_mistakes_question_id_fkey" FOREIGN KEY ("question_id") REFERENCES "questions"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "user_gamification" ADD CONSTRAINT "user_gamification_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "idempotency_records" ADD CONSTRAINT "idempotency_records_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "subscriptions" ADD CONSTRAINT "subscriptions_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "tryout_package_questions" ADD CONSTRAINT "tryout_package_questions_package_id_fkey" FOREIGN KEY ("package_id") REFERENCES "tryout_packages"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "tryout_package_questions" ADD CONSTRAINT "tryout_package_questions_question_id_fkey" FOREIGN KEY ("question_id") REFERENCES "questions"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "tryout_attempts" ADD CONSTRAINT "tryout_attempts_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "tryout_attempts" ADD CONSTRAINT "tryout_attempts_package_id_fkey" FOREIGN KEY ("package_id") REFERENCES "tryout_packages"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
