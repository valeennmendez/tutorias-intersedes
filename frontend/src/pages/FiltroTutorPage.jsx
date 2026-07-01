import { useEffect, useMemo, useState } from "react";
import {
	CalendarDays,
	ChevronDown,
	ChevronUp,
	ExternalLink,
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

import {
	Dialog,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle,
} from "@/components/ui/dialog";

import { Button } from "@/components/ui/button";
import { axiosInstance } from "@/utils/axios";
import { useAuthStore } from "@/store/auth.store";
import toast from "react-hot-toast";

function formatearFecha(fecha) {
	if (!fecha) return "";
	const [anio, mes, dia] = fecha.split("-");
	const meses = ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"];
	return `${dia} ${meses[parseInt(mes) - 1]} ${anio}`;
}

const DIAS_SEMANA = ["Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"];

export default function FiltroTutorPage() {
	const { user } = useAuthStore();
	const [busqueda, setBusqueda] = useState("");
	const [tutorias, setTutorias] = useState([]);
	const [cargando, setCargando] = useState(true);
	const [tutorExpandido, setTutorExpandido] = useState(null);
	const [inscripcionesActivas, setInscripcionesActivas] = useState(new Set());
	const [cargandoInscripciones, setCargandoInscripciones] = useState(true);
	const [dialogoAbierto, setDialogoAbierto] = useState(null);
	const [inscribiendo, setInscribiendo] = useState(false);

	useEffect(() => {
		const cargarTutorias = async () => {
			try {
				setCargando(true);
				const response = await axiosInstance.get("/tutorias");
				console.log("Tutorías recibidas:", response.data);
				setTutorias(response.data || []);
			} catch (error) {
				console.error("Error al cargar tutorías:", error);
			} finally {
				setCargando(false);
			}
		};

		cargarTutorias();
	}, []);

	useEffect(() => {
		const cargarInscripciones = async () => {
			if (user?.role !== "alumno") {
				setCargandoInscripciones(false);
				return;
			}
			try {
				setCargandoInscripciones(true);
				const res = await axiosInstance.get("/inscripciones/mis-inscripciones");
				const activas = new Set(
					(res.data || [])
						.filter((i) => i.status === "ACTIVA")
						.map((i) => i.tutoriaId)
				);
				setInscripcionesActivas(activas);
			} catch (error) {
				console.error("Error al cargar inscripciones:", error);
			} finally {
				setCargandoInscripciones(false);
			}
		};
		cargarInscripciones();
	}, [user]);

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

	const handleInscribirse = async (tutoriaId) => {
		setInscribiendo(true);
		try {
			await axiosInstance.post(`/inscripciones/tutoria/${tutoriaId}`);
			toast.success("¡Inscripción exitosa!");
			setInscripcionesActivas((prev) => new Set([...prev, tutoriaId]));
			setDialogoAbierto(null);
		} catch (error) {
			const data = error.response?.data;
			console.error("Error al inscribirse:", error.response || error);
			const msg =
				data?.detalle ||
				data?.error ||
				error.message ||
				"Error al inscribirse";
			toast.error(msg);
		} finally {
			setInscribiendo(false);
		}
	};

	const tutoriaDialogo = tutorias.find((t) => t.id === dialogoAbierto);

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
															<div className="flex flex-col gap-1 text-sm">
																<p className="text-slate-600">{tutoria.nombre}</p>
																<p className="text-slate-700">
																	<span className="font-medium">Tutor:</span> {tutoria.tutorNombre}
																</p>
																{tutoria.linkDrive ? (
																	<a
																		href={tutoria.linkDrive}
																		target="_blank"
																		rel="noopener noreferrer"
																		className="inline-flex items-center gap-1 text-[#008BBA] hover:underline"
																	>
																		<ExternalLink className="h-3.5 w-3.5" />
																		Contenido de Drive
																	</a>
																) : null}
															</div>

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
																{user?.role === "alumno" ? (
																	inscripcionesActivas.has(tutoria.id) ? (
																		<Button
																			type="button"
																			variant="outline"
																			disabled
																			className="border-slate-300 text-slate-400 cursor-not-allowed"
																		>
																			Ya inscripto
																		</Button>
																	) : (
																		<Button
																			type="button"
																			variant="outline"
																			className="border-emerald-600 text-emerald-700 hover:bg-emerald-50"
																			onClick={() => setDialogoAbierto(tutoria.id)}
																			disabled={cargandoInscripciones}
																		>
																			{cargandoInscripciones ? (
																				<Loader2 className="h-4 w-4 animate-spin" />
																			) : (
																				"Inscribirse"
																			)}
																		</Button>
																	)
																) : null}
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

																	{tutoria.ubicacion ? (
																		<div>
																			<p className="font-semibold text-slate-900">Ubicación</p>
																			<p>{tutoria.ubicacion}</p>
																		</div>
																	) : null}
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

			<Dialog open={dialogoAbierto !== null} onOpenChange={(open) => { if (!open) setDialogoAbierto(null); }}>
				{tutoriaDialogo ? (
					<DialogContent>
						<DialogHeader>
							<DialogTitle>Confirmar inscripción</DialogTitle>
							<DialogDescription>
								¿Estás seguro que querés inscribirte a esta tutoría?
							</DialogDescription>
						</DialogHeader>
						<div className="space-y-3 py-2">
							<div className="grid grid-cols-2 gap-2 text-sm">
								<div>
									<p className="font-semibold text-slate-900">Materia</p>
									<p className="text-slate-600">{tutoriaDialogo.materiaNombre}</p>
								</div>
								<div>
									<p className="font-semibold text-slate-900">Tutor</p>
									<p className="text-slate-600">{tutoriaDialogo.tutorNombre}</p>
								</div>
								<div>
									<p className="font-semibold text-slate-900">Fecha</p>
									<p className="text-slate-600">{formatearFecha(tutoriaDialogo.fecha)}</p>
								</div>
								<div>
									<p className="font-semibold text-slate-900">Horario</p>
									<p className="text-slate-600">
										{tutoriaDialogo.horaInicio?.slice(0, 5)} - {tutoriaDialogo.horaFin?.slice(0, 5)}
									</p>
								</div>
								<div>
									<p className="font-semibold text-slate-900">Sede</p>
									<p className="text-slate-600">{tutoriaDialogo.sede}</p>
								</div>
								<div>
									<p className="font-semibold text-slate-900">Modalidad</p>
									<p className="text-slate-600">{tutoriaDialogo.modalidad}</p>
								</div>
							</div>
						</div>
						<DialogFooter>
							<Button variant="outline" onClick={() => setDialogoAbierto(null)}>
								Cancelar
							</Button>
							<Button
								className="bg-emerald-600 text-white hover:bg-emerald-700"
								onClick={() => handleInscribirse(tutoriaDialogo.id)}
								disabled={inscribiendo}
							>
								{inscribiendo ? (
									<Loader2 className="mr-2 h-4 w-4 animate-spin" />
								) : null}
								Confirmar inscripción
							</Button>
						</DialogFooter>
					</DialogContent>
				) : null}
			</Dialog>
		</div>
	);
}