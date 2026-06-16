import { Outlet } from "react-router-dom";
import { Header } from "../header";

function LayoutNavbar() {
	return (
		<>
			<Header />
			<main>
				<Outlet />
			</main>
		</>
	);
}

export default LayoutNavbar;
