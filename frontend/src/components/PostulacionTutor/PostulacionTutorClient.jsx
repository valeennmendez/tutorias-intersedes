import { useState } from "react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { 
  GraduationCap, 
  BookOpen,
  Clock,
  CheckCircle,
  XCircle,
  Loader2,
  AlertCircle,
  Award,
  Users,
  Send
} from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";

const HARDCODED_PROFILE = {
  id: "user-123",
  nombre: "Juan Pérez",
  email: "juan@example.com",
};

const HARDCODED_MATERIAS = [
  { id: "mat-1", nombre: "Cálculo I", codigo: "MAT-101", anio: 1 },
  { id: "mat-2", nombre: "Álgebra Lineal", codigo: "MAT-102", anio: 1 },
  { id: "mat-3", nombre: "Física I", codigo: "FIS-101", anio: 1 },
  { id: "mat-4", nombre: "Programación II", codigo: "INF-102", anio: 2 },
  { id: "mat-5", nombre: "Bases de Datos", codigo: "INF-201", anio: 2 },
];

const HARDCODED_APPLICATIONS = [
  {
    id: "app-1",
    user_id: "user-123",
    materia_id: "mat-1",
    justificacion:
      "Me desempeñé muy bien en Cálculo I y me gustaría ayudar a otros estudiantes a entender los conceptos.",
    nota_aprobacion: 8.5,
    status: "pendiente",
    admin_comentario: null,
    created_at: "2026-05-15T14:30:00.000Z",
    materia: { nombre: "Cálculo I", codigo: "MAT-101" },
  },
  {
    id: "app-2",
    user_id: "user-123",
    materia_id: "mat-2",
    justificacion:
      "El álgebra es mi fuerte, quiero compartir métodos de resolución que me ayudaron a aprobar.",
    nota_aprobacion: 9,
    status: "aprobado",
    admin_comentario: "Excelente perfil, bienvenido como tutor.",
    created_at: "2026-04-02T10:15:00.000Z",
    materia: { nombre: "Álgebra Lineal", codigo: "MAT-102" },
  },
  {
    id: "app-3",
    user_id: "user-123",
    materia_id: "mat-3",
    justificacion:
      "Tengo experiencia dando clases de apoyo en Física y me siento preparado para tutorías.",
    nota_aprobacion: 6,
    status: "rechazado",
    admin_comentario: "La nota no alcanza el mínimo requerido para esta materia.",
    created_at: "2026-03-20T09:00:00.000Z",
    materia: { nombre: "Física I", codigo: "FIS-101" },
  },
];

export function PostulacionTutorClient() {
  const profile = HARDCODED_PROFILE;
  const materias = HARDCODED_MATERIAS;
  const applications = HARDCODED_APPLICATIONS;
  const userId = HARDCODED_PROFILE.id;

  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    materia_id: "",
    justificacion: "",
    nota_aprobacion: "",
  });

  const appliedMateriaIds = new Set(applications.map((a) => a.materia_id));
  const availableMaterias = materias.filter((m) => !appliedMateriaIds.has(m.id));

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.materia_id) {
      alert("Debes seleccionar una materia");
      return;
    }

    if (!formData.justificacion.trim()) {
      alert("Debes escribir una justificación");
      return;
    }

    if (formData.justificacion.length < 50) {
      alert("La justificación debe tener al menos 50 caracteres");
      return;
    }

    const nota = parseFloat(formData.nota_aprobacion);
    if (isNaN(nota) || nota < 4 || nota > 10) {
      alert("La nota debe estar entre 4 y 10");
      return;
    }

    setLoading(true);

    try {
      console.log("Postulación enviada (mock):", {
        user_id: userId,
        materia_id: formData.materia_id,
        justificacion: formData.justificacion.trim(),
        nota_aprobacion: nota,
        status: "pendiente",
      });

      alert("Postulación enviada correctamente");
      setFormData({ materia_id: "", justificacion: "", nota_aprobacion: "" });
    } catch (error) {
      console.error("Error al enviar la postulación:", error);
      alert("Error al enviar la postulación");
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case "pendiente":
        return (
          <Badge variant="secondary" className="gap-1">
            <Clock className="h-3 w-3" />
            Pendiente
          </Badge>
        );
      case "aprobado":
        return (
          <Badge className="bg-green-600 gap-1 text-white hover:bg-green-700">
            <CheckCircle className="h-3 w-3" />
            Aprobado
          </Badge>
        );
      case "rechazado":
        return (
          <Badge variant="destructive" className="gap-1">
            <XCircle className="h-3 w-3" />
            Rechazado
          </Badge>
        );
      default:
        return <Badge variant="outline">{status}</Badge>;
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Postulación como Tutor</h1>
        <p className="text-muted-foreground mt-2">
          Postúlate para ser tutor en las materias que hayas aprobado
        </p>
      </div>

      {/* Benefits */}
      <div className="grid gap-4 sm:grid-cols-3">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-sky-100">
                <Award className="h-5 w-5 text-sky-600" />
              </div>
              <div>
                <h4 className="font-medium">Certificados</h4>
                <p className="text-sm text-muted-foreground">Obtén certificados oficiales</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-sky-100">
                <Users className="h-5 w-5 text-sky-600" />
              </div>
              <div>
                <h4 className="font-medium">Ayuda a otros</h4>
                <p className="text-sm text-muted-foreground">Comparte tu conocimiento</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-sky-100">
                <GraduationCap className="h-5 w-5 text-sky-600" />
              </div>
              <div>
                <h4 className="font-medium">Experiencia</h4>
                <p className="text-sm text-muted-foreground">Desarrolla habilidades</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        {/* Application Form */}
        <Card>
          <CardHeader>
            <CardTitle>Nueva Postulación</CardTitle>
            <CardDescription>
              Completa el formulario para postularte como tutor
            </CardDescription>
          </CardHeader>
          <CardContent>
            {availableMaterias.length === 0 ? (
              <Alert>
                <AlertCircle className="h-4 w-4" />
                <AlertDescription>
                  Ya te has postulado para todas las materias disponibles o no hay materias en el sistema.
                </AlertDescription>
              </Alert>
            ) : (
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="materia">Materia</Label>
                  <Select
                    value={formData.materia_id}
                    onValueChange={(value) => setFormData({ ...formData, materia_id: value })}
                  >
                    <SelectTrigger>
                      <BookOpen className="h-4 w-4 mr-2 text-muted-foreground" />
                      <SelectValue placeholder="Selecciona una materia" />
                    </SelectTrigger>
                    <SelectContent>
                      {availableMaterias.map((materia) => (
                        <SelectItem key={materia.id} value={materia.id}>
                          {materia.nombre} ({materia.anio}° año)
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="nota">Nota con la que aprobaste (4-10)</Label>
                  <Input
                    id="nota"
                    type="number"
                    min="4"
                    max="10"
                    step="0.5"
                    placeholder="Ej: 7.5"
                    value={formData.nota_aprobacion}
                    onChange={(e) => setFormData({ ...formData, nota_aprobacion: e.target.value })}
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="justificacion">¿Por qué quieres ser tutor de esta materia?</Label>
                  <Textarea
                    id="justificacion"
                    placeholder="Describe tu experiencia con la materia, por qué te gustaría ayudar a otros estudiantes, y cualquier experiencia relevante que tengas..."
                    value={formData.justificacion}
                    onChange={(e) => setFormData({ ...formData, justificacion: e.target.value })}
                    rows={4}
                    required
                  />
                  <p className="text-xs text-muted-foreground">
                    {formData.justificacion.length}/50 caracteres mínimo. Sé específico sobre tu experiencia.
                  </p>
                </div>

                <Button type="submit" className="w-full bg-sky-500 hover:bg-sky-600 text-white" disabled={loading || formData.justificacion.length < 50}>
                  {loading ? (
                    <Loader2 className="h-4 w-4 animate-spin mr-2" />
                  ) : (
                    <Send className="h-4 w-4 mr-2" />
                  )}
                  Enviar Postulación
                </Button>
              </form>
            )}
          </CardContent>
        </Card>

        {/* Applications List */}
        <Card>
          <CardHeader>
            <CardTitle>Mis Postulaciones</CardTitle>
            <CardDescription>
              Historial de tus postulaciones como tutor
            </CardDescription>
          </CardHeader>
          <CardContent>
            {applications.length > 0 ? (
              <div className="space-y-4">
                {applications.map((application) => (
                  <div
                    key={application.id}
                    className="rounded-lg border p-4 space-y-3"
                  >
                    <div className="flex items-start justify-between">
                      <div>
                        <h4 className="font-medium">{application.materia?.nombre}</h4>
                        <p className="text-sm text-muted-foreground">
                          Nota: {application.nota_aprobacion}
                        </p>
                      </div>
                      {getStatusBadge(application.status)}
                    </div>
                    <p className="text-sm text-muted-foreground line-clamp-2">
                      {application.justificacion}
                    </p>
                    {application.admin_comentario && (
                      <div className="rounded bg-muted p-2">
                        <p className="text-xs font-medium">Comentario del administrador:</p>
                        <p className="text-sm text-muted-foreground">{application.admin_comentario}</p>
                      </div>
                    )}
                    <p className="text-xs text-muted-foreground">
                      Enviada el {format(new Date(application.created_at), "d 'de' MMMM, yyyy", { locale: es })}
                    </p>
                  </div>
                ))}
              </div>
            ) : (
              <div className="flex flex-col items-center justify-center py-8 text-center">
                <GraduationCap className="h-12 w-12 text-muted-foreground/50 mb-4" />
                <p className="text-muted-foreground">No tienes postulaciones</p>
                <p className="text-sm text-muted-foreground">
                  Envía tu primera postulación para ser tutor
                </p>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}