import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Access your configuration keys safely
SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_ANON_KEY = os.getenv("SUPABASE_ANON_KEY")
JWT_SECRET_KEY = os.getenv("JWT_SECRET_KEY")
AGENT_WEBHOOK_SECRET = os.getenv("AGENT_WEBHOOK_SECRET")
STRIPE_SECRET_KEY = os.getenv("STRIPE_SECRET_KEY")
