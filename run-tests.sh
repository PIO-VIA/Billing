#!/bin/bash

# Script pour exécuter les tests multi-tenancy

echo "╔═══════════════════════════════════════════════════════════╗"
echo "║    TESTS DE LA COUCHE MULTI-TENANCY                      ║"
echo "╚═══════════════════════════════════════════════════════════╝"
echo ""

# Couleurs
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction pour afficher un message
print_step() {
    echo -e "${BLUE}▶ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# Vérifier que Maven est installé
if ! command -v mvn &> /dev/null; then
    print_error "Maven n'est pas installé!"
    exit 1
fi

print_success "Maven détecté: $(mvn -version | head -1)"
echo ""

# Option 1: Tests rapides uniquement
if [ "$1" = "quick" ]; then
    print_step "MODE RAPIDE: Tests unitaires uniquement"
    mvn test -Dtest=OrganizationFilterTest
    exit $?
fi

# Option 2: Tests complets
print_step "1/4 - Nettoyage du projet..."
mvn clean > /dev/null 2>&1
if [ $? -eq 0 ]; then
    print_success "Nettoyage réussi"
else
    print_error "Échec du nettoyage"
    exit 1
fi
echo ""

print_step "2/4 - Compilation du projet..."
mvn compile -DskipTests > /dev/null 2>&1
if [ $? -eq 0 ]; then
    print_success "Compilation réussie"
else
    print_error "Échec de compilation"
    echo ""
    print_warning "Exécution avec logs pour voir les erreurs:"
    mvn compile -DskipTests
    exit 1
fi
echo ""

print_step "3/4 - Compilation des tests..."
mvn test-compile > /dev/null 2>&1
if [ $? -eq 0 ]; then
    print_success "Compilation des tests réussie"
else
    print_error "Échec de compilation des tests"
    mvn test-compile
    exit 1
fi
echo ""

print_step "4/4 - Exécution des tests multi-tenancy..."
echo ""
echo "═══════════════════════════════════════════════════════════"
echo "  TEST 1: Tests unitaires du filtre"
echo "═══════════════════════════════════════════════════════════"
mvn test -Dtest=OrganizationFilterTest

FILTER_TEST_RESULT=$?

echo ""
echo "═══════════════════════════════════════════════════════════"
echo "  TEST 2: Tests d'intégration complets"
echo "═══════════════════════════════════════════════════════════"
mvn test -Dtest=MultiTenancyIntegrationTest

INTEGRATION_TEST_RESULT=$?

echo ""
echo "╔═══════════════════════════════════════════════════════════╗"
echo "║                    RÉSULTATS                              ║"
echo "╚═══════════════════════════════════════════════════════════╝"
echo ""

if [ $FILTER_TEST_RESULT -eq 0 ]; then
    print_success "Tests du filtre: RÉUSSIS"
else
    print_error "Tests du filtre: ÉCHOUÉS"
fi

if [ $INTEGRATION_TEST_RESULT -eq 0 ]; then
    print_success "Tests d'intégration: RÉUSSIS"
else
    print_error "Tests d'intégration: ÉCHOUÉS"
fi

echo ""

if [ $FILTER_TEST_RESULT -eq 0 ] && [ $INTEGRATION_TEST_RESULT -eq 0 ]; then
    echo -e "${GREEN}"
    echo "╔═══════════════════════════════════════════════════════════╗"
    echo "║                                                           ║"
    echo "║   🎉 TOUS LES TESTS SONT PASSÉS AVEC SUCCÈS! 🎉          ║"
    echo "║                                                           ║"
    echo "║   La couche multi-tenant fonctionne correctement:        ║"
    echo "║   ✅ Isolation des données                               ║"
    echo "║   ✅ Filtrage automatique Hibernate                      ║"
    echo "║   ✅ Validation de sécurité                              ║"
    echo "║   ✅ Endpoints API fonctionnels                          ║"
    echo "║                                                           ║"
    echo "╚═══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
    echo ""
    print_step "Prochaines étapes:"
    echo "  1. Mettre à jour vos services existants (voir docs/SERVICE_INTEGRATION_EXAMPLE.md)"
    echo "  2. Tester manuellement avec Postman (voir TEST_INSTRUCTIONS.md)"
    echo "  3. Intégrer l'authentification JWT"
    echo ""
    exit 0
else
    echo -e "${RED}"
    echo "╔═══════════════════════════════════════════════════════════╗"
    echo "║                                                           ║"
    echo "║   ⚠️  CERTAINS TESTS ONT ÉCHOUÉ  ⚠️                      ║"
    echo "║                                                           ║"
    echo "╚═══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
    echo ""
    print_warning "Consultez les logs ci-dessus pour identifier les problèmes"
    print_warning "Voir TEST_INSTRUCTIONS.md pour le dépannage"
    echo ""
    exit 1
fi
