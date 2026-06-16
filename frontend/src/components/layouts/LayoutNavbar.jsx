import { Outlet } from "react-router-dom";
import { DashboardHeader } from "../header";
import { useAuthStore } from "@/store/auth.store";

function LayoutNavbar() {
	const { user } = useAuthStore();

	return (
		<>
			{user && <DashboardHeader profile={user} />}
			<main>
				<Outlet />
			</main>
		</>
	);
}

export default LayoutNavbar;
