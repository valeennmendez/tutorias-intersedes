import { Outlet } from "react-router-dom";
import { DashboardHeader } from "../header";

function LayoutNavbar() {
	return (
		<>
			<DashboardHeader profile={"admin"} />
			<main>
				<Outlet />
			</main>
		</>
	);
}

export default LayoutNavbar;
