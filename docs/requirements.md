### Requirements

#### Must-have Features (M)
	1.	SDK should initialize with an API key.
	2.	On initialization, the SDK collects user information, such as:
		•	IP address (via backend API, if necessary).
		•	Platform (e.g., iOS, Android).
		•	SDK version.
	3.	Provide a method expand(path: String): Metadata to fetch and parse metadata from the /expand API.
	4.	Shared codebase implemented in Kotlin Multiplatform (KMP) for iOS and Android.
	5.	Minimal dependency footprint to ensure lightweight SDK integration.
	6.	Handle errors gracefully (e.g., network issues, invalid paths).

#### Should-have Features (S)
	1.	Developer-friendly logging for debugging (optional debug mode).
	2.	Ensure thread safety for concurrent requests.
	3.	Automatic retries on transient errors.
	4.	Add hooks to enable future analytics (e.g., track clicks, installs, and referrals).

#### Could-have Features (C)
	1.	Cache link metadata locally for offline use (with TTL).
	2.	Pre-fetch metadata to reduce latency on expand calls.

#### Won't-have (W)
	1.	UI components for deep-link previews or routing.
	2.	Advanced analytics or dashboards in the first version.
	3.	Server-side functionality (e.g., link creation).