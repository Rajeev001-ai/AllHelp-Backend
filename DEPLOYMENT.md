# AllHelp Backend Deployment on Render

## Render Settings

Use these settings for the backend service:

```text
Environment: Docker
Root Directory: AllHelp
Dockerfile Path: Dockerfile
Health Check Path: /api/health
```

## Environment Variables

Set these values in Render. Do not commit real secrets.

```env
PORT=8081
DB_URL=jdbc:postgresql://YOUR_NEON_DIRECT_HOST/neondb?sslmode=require
DB_USERNAME=neondb_owner
DB_PASSWORD=your_database_password
FRONTEND_URL=https://your-vercel-frontend-domain.com
JWT_SECRET=replace_with_a_long_random_secret_at_least_32_characters
JWT_EXPIRATION_MS=86400000
ADMIN_USERNAME=admin
ADMIN_PASSWORD=replace_with_a_strong_admin_password
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
```

Use the Neon direct JDBC host for `DB_URL`, not the pooler URL, while Hibernate `ddl-auto=update` is enabled.

## Verify

After deployment, open:

```text
https://your-backend-domain.com/api/health
```

Expected response:

```text
Backend is running
```
