package uco.pensum.domain.repositories

import uco.pensum.infrastructure.mysql.database.PensumDatabase

class PrerequisitoRepository(
    implicit val provider: PensumDatabase
) {}
