import { useEffect, useMemo, useState } from "react";
import {
	CalendarDays,
	ChevronDown,
	ChevronUp,
	Loader2,
	Search,
} from "lucide-react";

import {
	Card,
	CardContent,
	CardDescription,
	CardHeader,
	CardTitle,
} from "@/components/ui/card";

import { Button } from "@/components/ui/button";
import { axiosInstance } from "@/utils/axios";

function formatearFecha(fecha) {
	if (!fecha) return "";
	const [anio, mes, dia] = fecha.split("-");
	const meses = ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"];
	return `${dia} ${meses[parseInt(mes) - 1]} ${anio}`;
}

const DIAS_SEMANA = ["Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"];

export default function FiltroTutorPage() {
	const [busqueda, setBusqueda] = useState("");
	const [tutorias, setTutorias] = useState([]);
	const [cargando, setCargando] = useState(true);
	const [tutorExpandido, setTutorExpandido] = useState(null);

	useEffect(() => {
		const cargarTutorias = async () => {
			try {
				setCargando(true);
				const response = await axiosInstance.get("/tutorias");
				setTutorias(response.data || []);
			} catch (error) {
				console.error("Error al cargar tutorías:", error);
			} finally {
				setCargando(false);
			}
		};

		cargarTutorias();
	}, []);

	const tutoriasFiltradas = useMemo(() => {
		const termino = busqueda.trim().toLowerCase();

		if (!termino) return tutorias;

		return tutorias.filter((tutoria) => {
			const textoBuscable = [
				tutoria.tutorNombre,
				tutoria.materiaNombre,
				tutoria.sede,
				tutoria.modalidad,
				tutoria.nombre,
			]
				.join(" ")
				.toLowerCase();

			return textoBuscable.includes(termino);
		});
	}, [busqueda, tutorias]);

	const toggleDetalles = (id) => {
		setTutorExpandido((actual) => (actual === id ? null : id));
	};

	return (
		<div className="min-h-screen bg-[#F7F9FB]">
			<div className="px-4 py-10">
				<div className="mx-auto flex w-full max-w-5xl flex-col gap-6">

					<Card className="border-slate-200 shadow-md">
						<CardHeader className="pb-4">
							<CardTitle className="text-xl text-slate-900">Buscar tutorías disponibles</CardTitle>
							<CardDescription>
								Escribí cualquier dato para filtrar las tutorías según la materia o el tutor.
							</CardDescription>
						</CardHeader>

						<CardContent className="space-y-5 pt-0">
							<div className="relative">
								<Search className="pointer-events-none absolute left-4 top-1/2 h-5 w-5 -translate-y-1/2 text-slate-400" />
								<input
									value={busqueda}
									onChange={(e) => setBusqueda(e.target.value)}
									type="text"
									placeholder="Buscar por materia, tutor, sede, horario o día..."
									className="h-12 w-full rounded-xl border border-slate-300 bg-white pl-12 pr-4 text-slate-900 shadow-sm outline-none transition focus:border-[#008BBA] focus:ring-2 focus:ring-[#008BBA]/20"
								/>
							</div>

							{cargando ? (
								<div className="flex justify-center py-12">
									<Loader2 className="h-8 w-8 animate-spin text-slate-400" />
								</div>
							) : (
								<>
									<div className="flex items-center justify-between gap-3 rounded-xl bg-slate-50 px-4 py-3 text-sm text-slate-600">
										<span>
											{tutoriasFiltradas.length} tutoría{tutoriasFiltradas.length === 1 ? "" : "s"} encontrada{tutoriasFiltradas.length === 1 ? "" : "s"}
										</span>
										<span className="hidden sm:inline">Hacé click en "Ver detalles" para ver más información.</span>
									</div>

									<div className="grid gap-4">
										{tutoriasFiltradas.length === 0 ? (
											<div className="rounded-xl border border-dashed border-slate-300 bg-white p-8 text-center text-slate-600">
												No se encontraron tutorías con ese criterio de búsqueda.
											</div>
										) : (
											tutoriasFiltradas.map((tutoria) => {
												const abierto = tutorExpandido === tutoria.id;
												const diaSemana = tutoria.fecha ? DIAS_SEMANA[new Date(tutoria.fecha).getDay()] : "";
												const horario = tutoria.horaInicio && tutoria.horaFin
													? `${tutoria.horaInicio.slice(0, 5)} - ${tutoria.horaFin.slice(0, 5)}`
													: "";

												return (
													<Card key={tutoria.id} className="border-slate-200 shadow-sm">
														<CardHeader className="pb-3">
															<div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
																<div>
																	<CardTitle className="text-xl text-slate-900">
																		{tutoria.materiaNombre}
																	</CardTitle>
																	<CardDescription>{tutoria.tutorNombre}</CardDescription>
																</div>

																<div className="text-left sm:text-right">
																	<span className="inline-flex rounded-full bg-sky-100 px-3 py-1 text-xs font-semibold text-[#008BBA]">
																		{tutoria.modalidad}
																	</span>
																	<p className="mt-2 text-sm text-slate-600">{tutoria.sede}</p>
																</div>
															</div>
														</CardHeader>

														<CardContent className="space-y-4 pt-0">
															<p className="text-sm text-slate-600">{tutoria.nombre}</p>

															<div className="flex gap-2">
																<Button
																	type="button"
																	onClick={() => toggleDetalles(tutoria.id)}
																	className="bg-[#008BBA] text-white hover:bg-[#00779f]"
																>
																	{abierto ? (
																		<>
																			<ChevronUp className="mr-2 h-4 w-4" />
																			Ocultar detalles
																		</>
																	) : (
																		<>
																			<ChevronDown className="mr-2 h-4 w-4" />
																			Ver detalles
																		</>
																	)}
																</Button>
																<Button
																	type="button"
																	variant="outline"
																	className="border-emerald-600 text-emerald-700 hover:bg-emerald-50"
																>
																	Inscribirse
																</Button>
															</div>

															{abierto ? (
																<div className="grid gap-3 rounded-xl bg-slate-50 p-4 text-sm text-slate-700 sm:grid-cols-2">
																	<div>
																		<p className="font-semibold text-slate-900">Materia</p>
																		<p>{tutoria.materiaNombre}</p>
																	</div>

																	<div>
																		<p className="font-semibold text-slate-900">Fecha</p>
																		<p>{formatearFecha(tutoria.fecha)}</p>
																	</div>

																	<div>
																		<p className="font-semibold text-slate-900">Horario</p>
																		<p>{horario}</p>
																	</div>

																	<div>
																		<p className="font-semibold text-slate-900">Día</p>
																		<span className="inline-flex rounded-full bg-white px-3 py-1 text-xs font-medium text-slate-700 shadow-sm">
																			<CalendarDays className="mr-1 inline h-3.5 w-3.5 text-[#008BBA]" />
																			{diaSemana}
																		</span>
																	</div>

																	<div>
																		<p className="font-semibold text-slate-900">Sede</p>
																		<p>{tutoria.sede}</p>
																	</div>

																	<div>
																		<p className="font-semibold text-slate-900">Modalidad</p>
																		<p>{tutoria.modalidad}</p>
																	</div>
																</div>
															) : null}
														</CardContent>
													</Card>
												);
											})
										)}
									</div>
								</>
							)}
						</CardContent>
					</Card>
				</div>
			</div>
		</div>
	);
}
