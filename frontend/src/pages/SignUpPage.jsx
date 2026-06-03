import { Hash, Lock, Mail, User } from "lucide-react";
import { useNavigate } from "react-router-dom";

function SignUpPage() {
	const navigate = useNavigate();

	return (
		<div className="bg-[#F7F9FB] h-screen w-screen">
			<div className="flex flex-col w-full h-full justify-center items-center gap-3">
				<div className="flex flex-col items-center">
					<div>
						<img src="/logo-unnoba.png" alt="logo-unnoba" className="rounded-xl w-25" />
					</div>
					<div className="text-center">
						<h1 className="font-bold text-2xl">Crear Cuenta</h1>
						<h2 className="font-normal text-slate-600 text-sm">Sistema de Tutorías UNNOBA</h2>
					</div>
				</div>
				<div className="bg-white rounded-lg shadow-md w-120 min-h-80 py-3">
					<div className="flex flex-col items-center">
						<h1 className="font-semibold text-xl">Registro</h1>
						<h2 className="text-slate-600 font-medium text-sm">Completa tus datos para crear tu cuenta</h2>
					</div>
					<div className="px-5 mt-4">
						<form action="">
							<div className="grid grid-cols-2 gap-5">
								<div className="flex flex-col relative gap-1">
									<span className="font-semibold text-slate-800 text-md">Nombre</span>
									<User className="absolute bottom-1.5 left-4 size-5 text-slate-700" />
									<input
										type="text"
										className="border shadow-sm font-medium border-slate-300 rounded-md h-8.5 pl-12"
										placeholder="Juan"
									/>
								</div>
								<div className="flex flex-col relative gap-1">
									<span className="font-semibold text-slate-800 text-md">Apellido</span>
									<input
										type="text"
										className="border shadow-sm font-medium border-slate-300 rounded-md h-8.5 pl-3"
										placeholder="Perez"
									/>
								</div>
							</div>
							<div className="flex flex-col relative mt-3 gap-1">
								<span className="font-semibold text-slate-800 text-md">Correo Institucional</span>
								<Mail className="absolute bottom-1.5 left-4 size-5 text-slate-700" />
								<input
									type="email"
									className="border shadow-sm font-medium border-slate-300 rounded-md h-8.5 pl-12"
									placeholder="usuario@comunidad.unnoba.edu.ar"
								/>
							</div>
							<div className="flex flex-col relative mt-3 gap-1">
								<span className="font-semibold text-slate-800 text-md">Legajo / DNI</span>
								<Hash className="absolute bottom-1.5 left-4 size-5 text-slate-700" />
								<input
									type="number"
									className="border shadow-sm font-medium border-slate-300 rounded-md h-8.5 pl-12"
									placeholder="1234"
								/>
							</div>
							<div className="grid grid-cols-2 gap-5 mt-3">
								<div className="flex flex-col gap-1">
									<span className="font-semibold text-slate-800 text-md">Carrera</span>
									<select name="" id="" className="border shadow-sm font-medium border-slate-300 rounded-md h-8.5"></select>
								</div>
								<div className="flex flex-col gap-1">
									<span className="font-semibold text-slate-800 text-md">Año</span>
									<select name="" id="" className="border shadow-sm font-medium border-slate-300 rounded-md h-8.5"></select>
								</div>
							</div>
							<div className="flex flex-col relative mt-3">
								<span className="font-semibold text-slate-800 text-md">Contraseña</span>
								<Lock className="absolute bottom-1.5 left-4 size-5 text-slate-700" />
								<input
									type="password"
									className="border shadow-sm font-medium border-slate-300 rounded-md h-8.5 pl-12"
									placeholder="Mínimo 6 caracteres"
									minLength={6}
								/>
							</div>
							<div className="flex flex-col relative mt-3">
								<span className="font-semibold text-slate-800 text-md">Repetir Contraseña</span>
								<Lock className="absolute bottom-1.5 left-4 size-5 text-slate-700" />
								<input
									type="password"
									className="border shadow-sm font-medium border-slate-300 rounded-md h-8.5 pl-12"
									placeholder="Repite tu contraseña"
								/>
							</div>
							<div>
								<button type="submit" className="h-8.5 mt-4 w-full font-medium rounded-md bg-[#008BBA] text-white">
									Crear Cuenta
								</button>
							</div>
						</form>
						<div className="w-full flex justify-center mt-4  text-center">
							<h3 className="text-slate-600 text-sm font-medium">
								¿Ya tienes cuenta?{" "}
								<span className="text-[#008BBA]">
									<a href="" onClick={() => navigate("/login")}>
										Inicia Sesion
									</a>{" "}
								</span>
							</h3>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}

export default SignUpPage;
