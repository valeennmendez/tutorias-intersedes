import { Book, Calendar, MapPin, PenBoxIcon, Users2Icon, Video, VideoIcon } from "lucide-react";
import { useState } from "react";

function CrearTutoriaPage() {
	const [seleccion, setSeleccion] = useState(null);

	return (
		<div className="bg-[#F7F9FB] w-scree px-20 py-10 h-screen">
			<div>
				<h1 className="font-bold text-2xl">Nueva Tutoria</h1>
				<h3 className="text-slate-800 text-md">Crear nueva sesión de tutoría para tus compañeros</h3>
			</div>
			<div className="bg-white border mt-3 border-slate-200 px-10 py-5 rounded-lg w-full h-full">
				<div>
					<h3 className="font-semibold">Información de la Tutoría</h3>
					<h4 className="text-slate-500 text-sm">Completa los datos para crear una nueva sesión</h4>
				</div>
				<div className="mt-3">
					<form action="">
						<div className="flex flex-row justify-between ">
							<div className="flex flex-col relative mt-3 gap-1">
								<span className="font-semibold text-slate-800 text-md">Materia</span>
								<div className="border px-2 rounded-md shadow-sm border-slate-300 w-100">
									<Book className="absolute bottom-2 left-2.5 size-5 text-slate-500" />
									<select className=" font-medium border-slate-300 rounded-md w-full h-8.5 pl-8"></select>
								</div>
							</div>
							<div>
								<div className="flex flex-col relative mt-3 gap-1">
									<span className="font-semibold text-slate-800 text-md">Correo Institucional</span>
									<Users2Icon className="absolute bottom-1.5 left-4 size-5 text-slate-500" />
									<input
										type="number"
										className="border shadow-sm font-medium border-slate-300 rounded-md w-100 h-8.5 pl-12"
										placeholder="Ingresa el cupo máximo"
									/>
								</div>
							</div>
						</div>
						<div className="flex flex-col relative mt-3 gap-1">
							<span className="font-semibold text-slate-800 text-md">Titulo de la tutoria</span>
							<PenBoxIcon className="absolute bottom-1.5 left-4 size-5 text-slate-500" />
							<input
								type="text"
								className="border shadow-sm font-medium border-slate-300 rounded-md w-full h-8.5 pl-12"
								placeholder="Ej: Repaso para parcial de POO"
							/>
						</div>
						<div className="flex flex-col relative mt-3 gap-1">
							<span className="font-semibold text-slate-800 text-md">Descripción</span>
							<textarea
								className="border shadow-sm h-15 max-h-20 font-medium border-slate-300 rounded-md w-full  px-3 py-2"
								maxLength={200}
								placeholder="Describe que temas se van a tratar, que deben traer los estudiantes, etc. (Máx 200 caracteres)"
							/>
						</div>
						<div className="grid grid-cols-3 gap-5">
							<div className="flex flex-col relative mt-3 gap-1">
								<span className="font-semibold text-slate-800 text-md">Fecha</span>
								<div className="border px-2 rounded-md shadow-sm border-slate-300">
									<Calendar className="absolute bottom-2 left-2.5 size-5 text-slate-500" />
									<input
										type="date"
										className=" font-medium border-slate-300 rounded-md w-full h-8.5 pl-8"
										placeholder="Ej: Repaso para parcial de POO"
									/>
								</div>
							</div>
							<div className="flex flex-col relative mt-3 gap-1">
								<span className="font-semibold text-slate-800 text-md">Hora Inicio</span>
								<div className="border px-2 rounded-md shadow-sm border-slate-300">
									<Calendar className="absolute bottom-2 left-2.5 size-5 text-slate-500" />
									<input type="time" className=" font-medium border-slate-300 rounded-md w-full h-8.5 pl-8" />
								</div>
							</div>
							<div className="flex flex-col relative mt-3 gap-1">
								<span className="font-semibold text-slate-800 text-md">Hora Fin</span>
								<div className="border px-2 rounded-md shadow-sm border-slate-300">
									<Calendar className="absolute bottom-2 left-2.5 size-5 text-slate-500" />
									<input type="time" className=" font-medium border-slate-300 rounded-md w-full h-8.5 pl-8" />
								</div>
							</div>
						</div>
						<div className="mt-4">
							<span className="font-semibold text-slate-800 text-md">Modalidad</span>
							<div className="flex flex-row gap-3 ">
								<div
									className={`w-[50%] ${seleccion === "P" ? "bg-[#008BBA] text-white" : "bg-slate-100"}  h-25 border hover:bg-[#008BBA] hover:text-white cursor-pointer transition-colors flex  border-slate-300 rounded-md shadow-sm flex-col justify-center items-center`}
									onClick={() => setSeleccion("P")}
								>
									<MapPin />
									<span>Presencial</span>
								</div>
								<div
									className={`w-[50%] ${seleccion === "V" ? "bg-[#008BBA] text-white" : "bg-slate-100"}  h-25 border hover:bg-[#008BBA] hover:text-white cursor-pointer transition-colors border-slate-300 rounded-md shadow-sm flex flex-col justify-center items-center`}
									onClick={() => setSeleccion("V")}
								>
									<Video />
									<span>Virtual</span>
								</div>
							</div>
						</div>
						<div className={`${seleccion === null ? "hidden" : "flex flex-col"}  relative mt-3 gap-1`}>
							<span className="font-semibold text-slate-800 text-md">{seleccion === "P" ? "Ubiación" : "Link sala Meet"}</span>
							<div className="border px-2 rounded-md shadow-sm border-slate-300">
								{seleccion === "P" ? (
									<MapPin className="absolute bottom-2 left-2.5 size-5 text-slate-500" />
								) : (
									<VideoIcon className="absolute bottom-2 left-2.5 size-5 text-slate-500" />
								)}
								<input
									type="text"
									className=" font-medium border-slate-300 rounded-md w-full h-8.5 pl-8"
									placeholder={seleccion === "P" ? "Ej: Edificio Rivadavia Salon 1" : "Ingrese su sala de Google Meet"}
								/>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	);
}

export default CrearTutoriaPage;
